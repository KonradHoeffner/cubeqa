package org.aksw.cubeqa.template;

import java.util.*;
import org.aksw.cubeqa.Cube;
import org.aksw.cubeqa.property.ComponentProperty;
import org.aksw.cubeqa.property.scorer.ScoreResult;
import de.konradhoeffner.commons.Pair;
import lombok.*;

/** Result of a match on a part of the parse tree. Gets combined with other match results to a template fragment. */
@EqualsAndHashCode
@Getter
@ToString
class Match
{
	/** A phrase contained in the question.*/
	public final String phrase;
//	/** Character index of the start of the phrase in the original question. **/
//	public final int phraseIndex;
	/** the estimated probability that the phrase refers to a property with a given property label */
	public final Map<ComponentProperty,Double> nameRefs;
	/** the estimated probability that the phrase refers to a property with a given property value*/
	public final Map<ComponentProperty,ScoreResult> valueRefs;

	public final double score;

	public void join(Match otherResult)
	{
		Set<ComponentProperty> nameValue = this.nameRefs.keySet();
		nameValue.retainAll(otherResult.valueRefs.keySet());
		//			nameValue.retainAll(otherResult.valueRefs.stream().map(ScoreResult::getProperty).collect(Collectors.toSet()));

		nameValue.stream().map(property->new Pair<>(property,nameRefs.get(property)*valueRefs.get(property).score))
		.max(Comparator.comparing(Pair::getB));
	}

	public boolean isEmpty()
	{
		return nameRefs.isEmpty()&&valueRefs.isEmpty();
	}

	public Match(String phrase, /* int phraseIndex,*/ Map<ComponentProperty,Double> nameRefs,  Map<ComponentProperty,ScoreResult> valueRefs)
	{
		this.phrase = phrase;
//		this.phraseIndex=phraseIndex;
		this.nameRefs = nameRefs;
		this.valueRefs = valueRefs;
		score = Math.max(nameRefs.values().stream().reduce(0.0,Double::max),
				valueRefs.values().stream().mapToDouble(ScoreResult::getScore).max().orElse(0));
	}

	public Fragment toFragment(Cube cube)
	 {
		return new Fragment(cube, phrase, new HashSet<>(), new HashSet<>(),new HashSet<>(),new HashSet<>(), Collections.singleton(this));
	 }


}
