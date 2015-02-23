package org.aksw.cubeqa.template;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.aksw.cubeqa.Cube;
import org.aksw.cubeqa.property.ComponentProperty;
import org.aksw.cubeqa.property.scorer.ScoreResult;
import de.konradhoeffner.commons.Pair;

/** Result of a match on a part of the parse tree. Gets combined with other match results to a template fragment. */
@EqualsAndHashCode
@Getter
@ToString
class MatchResult
{
	public final String phrase;
	/** the estimated probability that the phrase refers to a property with a given property label */
	public final Map<ComponentProperty,Double> nameRefs;
	/** the estimated probability that the phrase refers to a property with a given property value*/
	public final Map<ComponentProperty,ScoreResult> valueRefs;

	public final double score;

	public void join(MatchResult otherResult)
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

	public MatchResult(String phrase, Map<ComponentProperty,Double> nameRefs,  Map<ComponentProperty,ScoreResult> valueRefs)
	{
		this.phrase = phrase;
		this.nameRefs = nameRefs;
		this.valueRefs = valueRefs;
		score = Math.max(nameRefs.values().stream().reduce(0.0,Double::max),
				valueRefs.values().stream().mapToDouble(ScoreResult::getScore).max().orElse(0));
	}

	public CubeTemplateFragment toFragment(Cube cube)
	 {
		return new CubeTemplateFragment(cube, phrase, new HashSet<>(), new HashSet<>(),new HashSet<>(),new HashSet<>(), Collections.singleton(this));
	 }


}