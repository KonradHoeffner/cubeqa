package org.aksw.cubeqa.template;

import static org.aksw.cubeqa.AnswerType.*;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import org.aksw.commons.util.StopWatch;
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
public class Fragment
{
	private static final double TO_TEMPLATE_VALUE_SCORE_THRESHOLD = 0.2;
	private static final double	MIN_COMBINED_SCORE	= 0.1;

	final Cube cube;
	final String phrase;

	private final Set<Restriction> restrictions;
	private final Set<ComponentProperty> answerProperties;
	private final Set<ComponentProperty> perProperties;
	private final Set<Aggregate> aggregates;
	private final Set<Match> matches;

	private Set<ComponentProperty> unreferredProperties()
	{
		Set<ComponentProperty> properties = new HashSet<>(cube.properties.values());
		properties.removeAll(restrictions.stream().map(Restriction::getProperty).collect(Collectors.toSet()));
		properties.removeAll(answerProperties);
		properties.removeAll(perProperties);
		return properties;
	}

	public Fragment(Cube cube, String phrase)
	{
		this(cube, phrase, new HashSet<>(),new HashSet<>(),new HashSet<>(),new HashSet<>(),new HashSet<>());
	}

	public static Fragment combine(Collection<Fragment> fragments)
	{
		StopWatch fragmentCombineWatch = StopWatches.INSTANCE.getWatch("fragmentcombine");
		fragmentCombineWatch.start();
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
		Set<Match> matchResults = new HashSet<>();
		fragments.forEach(f->
		{
			restrictions.addAll(f.restrictions);
			answerProperties.addAll(f.answerProperties);
			perProperties.addAll(f.perProperties);
			aggregates.addAll(f.aggregates);
		});
		// *** phrases are added in list order with space in between ***********************************************
		String combinedPhrase = fragments.stream().map(Fragment::getPhrase).reduce("", (a,b)->a+" "+b).trim();
		Fragment fragment = new Fragment(fragments.iterator().next().cube,combinedPhrase,
				restrictions, answerProperties, perProperties, aggregates,matchResults);

		// *** combining match results *****************************************************************************
		// **** get all properties that are not yet assigned but somewhere referenced both as name and as value
		// strictly, they should be referenced in different matchresult objects but that calculation would be too complicated, sort that out later
		Set<ComponentProperty> properties = fragment.unreferredProperties();
		Set<Match> fragmentsMatchResults = fragments.stream().map(Fragment::getMatches).map(Set::stream).flatMap(id->id).collect(Collectors.toSet());
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

		fragmentCombineWatch.stop();
		return fragment;
	}

	/** Transforms the final fragment, which likely has undergone many combinations with other fragments in the tree-based algorithm, to a template.
	 * There will be no further combining so all leftover matchresult values will have to be guessed where they fit or thrown away.
	 * For each phrase there can be at most one match and each match represents another phrase so we for each match result we take at most one reference.
	 * @param expectedAnswerTypes The expected answer types of the question based on its question word. Used to choose the answer property.
	 * @return An optional containing the cube template or an empty optional, if no answer property could identified.
	 */
	Optional<Template> toTemplate(EnumSet<AnswerType> expectedAnswerTypes)
	{
		// For values which are only referenced by value, not by property name.
		// Happens very often in practice (e.g. most people say "in 2010" and not "in the year of 2010") so I recommend to set the config parameter to true.
		if(Config.INSTANCE.findNamelessReferences)
		{
			for(Match mr: matches)
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
		if(answerProperties.isEmpty()) {answerProperties.add(findAnswerProperty(expectedAnswerTypes));}

		restrictions.stream().flatMap(r->r.orderLimitPatterns().stream()).collect(Collectors.toSet());
		if(aggregates.isEmpty()/*&&orderLimitPatterns.isEmpty()*/
				&&((answerProperties.iterator().next().answerType==AnswerType.UNCOUNTABLE)||(answerProperties.iterator().next().answerType==AnswerType.COUNTABLE))
//				&&(expectedAnswerTypes.contains(AnswerType.UNCOUNTABLE)||expectedAnswerTypes.contains(AnswerType.COUNTABLE))
//				&&!(answerProperties.stream().filter(p->p.propertyType!=PropertyType.MEASURE).findAny().isPresent())
				)
		{aggregates.add(Aggregate.SUM);}

		return Optional.of(new Template(cube, restrictions, answerProperties, perProperties,aggregates));
	}

	/** @param expectedAnswerTypes the set of expected answer types possible for the question word
	 * @return the most likely answer property based on those answer types.
	 * Iff there is no property with the correct answer type or {@link Config#useAnswerTypes} is false, the most likely of all properties is checked.
	 * Iff there are no candidates at all, a random measure is returned (or the default answer property iff {@link Config#useDefaultAnswerProperty} is true).*/
	private ComponentProperty findAnswerProperty(EnumSet<AnswerType> expectedAnswerTypes)
	{
		Set<ComponentProperty> candidates = matches.stream()
				.map(Match::getNameRefs)
				.map(Map::keySet)
				.flatMap(Set::stream)
				.collect(Collectors.toSet());
		candidates.removeAll(perProperties);

		if(candidates.isEmpty())
		{
			log.warn("no answer property candidate found...");
			if(Config.INSTANCE.useDefaultAnswerProperty)
			{
				log.warn("...using default answer property: "+cube.getDefaultAnswerProperty());
				return cube.getDefaultAnswerProperty();
			}
			Set<ComponentProperty> measures = cube.properties.values().stream().filter(p->p.propertyType==PropertyType.MEASURE).collect(Collectors.toSet());
			if(measures.isEmpty()) {throw new RuntimeException("Cube "+cube+" does not contain any measures, cannot answer the question.");}
			ComponentProperty measure = measures.iterator().next();
			log.warn("...using any of the "+measures.size()+" measures, choosing "+measure);
			return measure;
		}

		Set<ComponentProperty> fittingAnswerType = candidates.stream().filter(c->expectedAnswerTypes.contains(c.answerType)).collect(Collectors.toSet());

		if(!Config.INSTANCE.useAnswerTypes) {return best(candidates);}

		log.info(candidates.size()+" property name references found, "+fittingAnswerType+" with the right answer type.");

		if(fittingAnswerType.isEmpty())
		{
			// no property references with the right type, is it a count?
			EnumSet<AnswerType> canBeCounted = EnumSet.of(ENTITY,TEMPORAL,LOCATION);
			if(expectedAnswerTypes.contains(AnswerType.COUNT))
			{
				// TODO: is this stable? and can we select better?
				Optional<ComponentProperty> ocp = candidates.stream().filter(c->canBeCounted.contains(c.answerType)).findFirst();
				if(ocp.isPresent())
				{
					aggregates.add(Aggregate.COUNT);
					return ocp.get();
				}
			}
			// all our candidates have the wrong answer type but beggars can't be choosers, so use the best of them
			return best(candidates);
		}

		ComponentProperty best = best(fittingAnswerType);
		log.info("component property: "+best.uri+" with answer type "+best.answerType+" chosen out of "+fittingAnswerType.size()+ "candidates with the right answer type.");
		return best;
	}

	/** @return the property with the highest score in the match results or any one if none occurr there. */
	private ComponentProperty best(Set<ComponentProperty> properties)
	{
		return
				matches.stream()
				.map(Match::getNameRefs)
				.map(Map::entrySet)
				.flatMap(Set::stream)
				.filter(e->properties.contains(e.getKey()))
				.max(Comparator.comparing(e->e.getValue()))
				.map(Entry::getKey)
				// can't determine highest scored, take any
				.orElse(properties.iterator().next());
	}

	public boolean isEmpty()
	{
		return restrictions.isEmpty()&&answerProperties.isEmpty()&&perProperties.isEmpty()
				&&aggregates.isEmpty()&&matches.isEmpty();
	}
}