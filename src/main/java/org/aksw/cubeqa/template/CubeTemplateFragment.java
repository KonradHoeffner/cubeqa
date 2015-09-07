package org.aksw.cubeqa.template;

import java.util.*;
import java.util.stream.Collectors;
import org.aksw.cubeqa.*;
import org.aksw.cubeqa.detector.Aggregate;
import org.aksw.cubeqa.property.ComponentProperty;
import org.aksw.cubeqa.property.PropertyType;
import org.aksw.cubeqa.property.scorer.ScoreResult;
import org.aksw.cubeqa.restriction.Restriction;
import lombok.*;
import lombok.extern.log4j.Log4j;

@RequiredArgsConstructor
@EqualsAndHashCode
@Getter
@Log4j
@ToString(exclude="cube")
/** Unfinished template for a data cube query.
 * Gets combined with other fragments and finally converted to a template. */
public class CubeTemplateFragment
{
	private static final double TO_TEMPLATE_VALUE_SCORE_THRESHOLD = 0.2;
	private static final double	MIN_COMBINED_SCORE	= 0.1;

	final Cube cube;
	final String phrase;

	private final Set<Restriction> restrictions;
	private final Set<ComponentProperty> answerProperties;
	private final Set<ComponentProperty> perProperties;
	private final Set<Aggregate> aggregates;
	private final Set<MatchResult> matchResults;

	private Set<ComponentProperty> unreferredProperties()
	{
		Set<ComponentProperty> properties = new HashSet<>(cube.properties.values());
		properties.removeAll(restrictions.stream().map(Restriction::getProperty).collect(Collectors.toSet()));
		properties.removeAll(answerProperties);
		properties.removeAll(perProperties);
		return properties;
	}

	public CubeTemplateFragment(Cube cube, String phrase)
	{
		this(cube, phrase, new HashSet<>(),new HashSet<>(),new HashSet<>(),new HashSet<>(),new HashSet<>());
	}

	public static CubeTemplateFragment combine(Collection<CubeTemplateFragment> fragments)
	{
		//		fragments = fragments.stream().filter(f->!f.isEmpty()).collect(Collectors.toList());
		if(fragments.isEmpty())
		{throw new IllegalArgumentException("empty fragment set, can't combine");}
		//		{log.warn("empty fragment set, combination empty");}

		// *** new sets are unions over all fragment sets **********************************************************
		if(fragments.stream().map(f->f.cube.uri).collect(Collectors.toSet()).size()>1) {
			throw new IllegalArgumentException("different cube uris, can't combine");
		}
		// TODO join restrictions if possible (e.g. intervals for numericals, detect impossibilities)
		Set<Restriction> restrictions = new HashSet<>();
		Set<ComponentProperty> answerProperties = new HashSet<>();
		Set<ComponentProperty> perProperties = new HashSet<>();
		Set<Aggregate> aggregates = new HashSet<>();
		Set<MatchResult> matchResults = new HashSet<>();
		fragments.forEach(f->
		{
			restrictions.addAll(f.restrictions);
			answerProperties.addAll(f.answerProperties);
			perProperties.addAll(f.perProperties);
			aggregates.addAll(f.aggregates);
		});
		// *** phrases are added in list order with space in between ***********************************************
		String combinedPhrase = fragments.stream().map(CubeTemplateFragment::getPhrase).reduce("", (a,b)->a+" "+b).trim();
		CubeTemplateFragment fragment = new CubeTemplateFragment(fragments.iterator().next().cube,combinedPhrase,
				restrictions, answerProperties, perProperties, aggregates,matchResults);

		// *** combining match results *****************************************************************************
		// **** get all properties that are not yet assigned but somewhere referenced both as name and as value
		// strictly, they should be referenced in different matchresult objects but that calculation would be too complicated, sort that out later
		Set<ComponentProperty> properties = fragment.unreferredProperties();
		Set<MatchResult> fragmentsMatchResults = fragments.stream().map(CubeTemplateFragment::getMatchResults).map(Set::stream).flatMap(id->id).collect(Collectors.toSet());
		properties.retainAll(fragmentsMatchResults.stream().map(mr->mr.nameRefs.keySet()).flatMap(Set::stream).collect(Collectors.toSet()));
		properties.retainAll(fragmentsMatchResults .stream().map(mr->mr.valueRefs.keySet()).flatMap(Set::stream).collect(Collectors.toSet()));
		for(ComponentProperty property: properties)
		{
			// greedy algorithm, does not work when highestNameRef has the only value Ref TODO intelligently check more pairs
			// we should always get a highest name in the first iteration per construction of fragmentsMatchResults
			// but later this one can be used for another property, so use ifpresent

			fragmentsMatchResults.stream().max(Comparator.comparingDouble(mr->mr.nameRefs.get(property)==null?0:mr.nameRefs.get(property)))
			.ifPresent(highestNameRef->
			{
				fragmentsMatchResults.stream().filter(mr->mr!=highestNameRef)
				.max(Comparator.comparingDouble(mr->mr.valueRefs.get(property)==null?0:mr.valueRefs.get(property).score))
				.ifPresent(highestValueRef->
				{
					if(highestNameRef.nameRefs.get(property)!=null&&highestValueRef.valueRefs.get(property)!=null)
					{
						double score = highestNameRef.nameRefs.get(property)*highestValueRef.valueRefs.get(property).score;
						if(score>MIN_COMBINED_SCORE)
						{
							restrictions.add(highestValueRef.valueRefs.get(property).toRestriction());
							fragmentsMatchResults.remove(highestNameRef);
							fragmentsMatchResults.remove(highestValueRef);
						}
					}
				});
			});
		}
		// add back all non used match results
		matchResults.addAll(fragmentsMatchResults);
		// **** end combining match resuls *************************************************************************

		//		Set<ComponentProperty> nameValue = this.nameRefs.keySet();
		//		nameValue.retainAll(otherResult.valueRefs.keySet());

		return fragment;
	}

	/** Transforms the final fragment, which likely has undergone many combinations with other fragments in the tree-based algorithm, to a template.
	 * There will be no further combining so all leftover matchresult values will have to be guessed where they fit or thrown away.
	 * For each phrase there can be at most one match and each match represents another phrase so we for each match result we take at most one reference.
	 * @param expectedAnswerTypes The expected answer types of the question based on its question word. Used to choose the answer property.
	 * @return An optional containing the cube template or an empty optional, if no answer property could identified.
	 */
	Optional<CubeTemplate> toTemplate(EnumSet<AnswerType> expectedAnswerTypes)
	{
		// For values which are only referenced by value, not by property name.
		// Happens very often in practice (e.g. most people say "in 2010" and not "in the year of 2010") so I recommend to set the config parameter to true.
		if(Config.INSTANCE.findNamelessReferences)
		{
			for(MatchResult mr: matchResults)
			{
				// we only use those whose properties which are not already referred to
				// as unreferredProperties() is called in each iteration, it is up to date with new restrictions from former iterations
				mr.valueRefs.values().stream().filter(sr->unreferredProperties().contains(sr.property))
				.filter(sr->sr.score>TO_TEMPLATE_VALUE_SCORE_THRESHOLD)
				.max(Comparator.comparing(ScoreResult::getScore))
				.ifPresent(scoreResult->
				{
					log.debug("toTemplate: adding restriction "+scoreResult.toRestriction()+" from score result "+scoreResult);
					restrictions.add(scoreResult.toRestriction());
				});
			}
		}
		// do we have leftover name refs? use them as per properties
		Set<ComponentProperty> leftOverNamed = matchResults.stream().flatMap(mr->mr.nameRefs.keySet().stream()).collect(Collectors.toSet());
		perProperties.addAll(leftOverNamed);

		// if no answer property, search in match results for property refs that are compatible with the answer types
		if(answerProperties.isEmpty())
		{
			log.info("No answer properties defined, searching in unpaired property name references...");
			Set<ComponentProperty> candidates = matchResults.stream()
					.map(MatchResult::getNameRefs)
					.map(Map::keySet)
					.flatMap(Set::stream)
					.collect(Collectors.toSet());

			if(candidates.isEmpty())
			{
				log.warn("no answer property candidate found...");
				if(Config.INSTANCE.useDefaultAnswerProperty)
				{
					log.warn("...using default answer property: "+cube.getDefaultAnswerProperty());
					answerProperties.add(cube.getDefaultAnswerProperty());
				} else
				{
					Set<ComponentProperty> measures = cube.properties.values().stream().filter(p->p.propertyType==PropertyType.MEASURE).collect(Collectors.toSet());
					if(measures.isEmpty())
					{
						log.warn("Cube does not contain any measures, cannot answer the question.");

					} else
					{
						ComponentProperty measure = measures.iterator().next();
						log.warn("...using any of the "+measures.size()+" measures, choosing "+measure);
						answerProperties.add(measure);
					}
				}
			}

			Set<ComponentProperty> fittingAnswerType = candidates.stream()
					.filter(c->expectedAnswerTypes.contains(c.answerType))
					.collect(Collectors.toSet());


			log.info(candidates.size()+" property name references found, "+fittingAnswerType+" with the right answer type.");
			if(fittingAnswerType.size()==1)
			{
				ComponentProperty onlyProperty = fittingAnswerType.iterator().next();
				answerProperties.add(onlyProperty);
				log.info("only one reference with the correct answer type: "+onlyProperty+" with answer type "+onlyProperty.answerType);
			} else if(fittingAnswerType.size()==0)
			{
				// TODO look at all candidates even if their types don't fit, maybe doing a count or something?
			} else
			{
				// TODO multiple options, use the one with the highest value
			}
			//			matchResults.stream()
			//			.map(MatchResult::getNameRefs)
			//			.map(Map::entrySet)
			//			.flatMap(Set::stream)
			//			.filter(entry->entry.getKey().propertyType==PropertyType.MEASURE)
			//			.max(Comparator.comparing(e->e.getValue()))
			//			.ifPresent(e->answerProperties.add(e.getKey()));

		}

		return Optional.of(new CubeTemplate(cube, restrictions, answerProperties, perProperties,aggregates));
	}

	public boolean isEmpty()
	{
		return restrictions.isEmpty()&&answerProperties.isEmpty()&&perProperties.isEmpty()
				&&aggregates.isEmpty()&&matchResults.isEmpty();
	}
}