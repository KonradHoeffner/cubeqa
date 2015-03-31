package org.aksw.cubeqa.template;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.*;
import lombok.extern.log4j.Log4j;
import org.aksw.cubeqa.Aggregate;
import org.aksw.cubeqa.Cube;
import org.aksw.cubeqa.property.ComponentProperty;
import org.aksw.cubeqa.property.scorer.ScoreResult;
import org.aksw.cubeqa.restriction.Restriction;

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

	final Set<Restriction> restrictions;
	final Set<ComponentProperty> answerProperties;
	final Set<ComponentProperty> perProperties;
	final Set<Aggregate> aggregates;
	final Set<MatchResult> matchResults;

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

	public static CubeTemplateFragment combine(List<CubeTemplateFragment> fragments)
	{
//		fragments = fragments.stream().filter(f->!f.isEmpty()).collect(Collectors.toList());
		if(fragments.isEmpty())
		{throw new IllegalArgumentException("empty fragment set, can't combine");}
//		{log.warn("empty fragment set, combination empty");}

		// *** new sets are unions over all fragment sets **********************************************************
		if(fragments.stream().map(f->f.cube.uri).collect(Collectors.toSet()).size()>1) throw new IllegalArgumentException("different cube uris, can't combine");
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


	CubeTemplate toTemplate()
	{
		// there will be no further combining so all leftover matchresult values will have to be guessed where they fit or thrown away

		// for each phrase there can be at most one match and each match represents another phrase so we for each match result we take at most one reference

		//		Set<ScoreResult> leftOverResults =  matchResults.stream().map(MatchResult::getValueRefs).map(Map::values).flatMap(Collection::stream).collect(Collectors.toSet());
		// try to get more restrictions
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
		// if no answer property, search in match results for property refs and take the highest scored, if no property refs take default one
		if(answerProperties.isEmpty())
		{
			matchResults.stream().map(MatchResult::getNameRefs).map(Map::entrySet).flatMap(Set::stream).max(Comparator.comparing(e->e.getValue()))
			.ifPresent(e->answerProperties.add(e.getKey()));
			if(answerProperties.isEmpty())
			{
				log.debug("no answer property found, using default of "+cube.getDefaultAnswerProperty());
				answerProperties.add(cube.getDefaultAnswerProperty());
			}
		}
		return new CubeTemplate(cube, restrictions, answerProperties, perProperties,aggregates);
	}

	public boolean isEmpty()
	{
		return restrictions.isEmpty()&&answerProperties.isEmpty()&&perProperties.isEmpty()
				&&aggregates.isEmpty()&&matchResults.isEmpty();
	}
}