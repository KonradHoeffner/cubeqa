package org.aksw.autosparql.cube.template;

import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import org.aksw.autosparql.cube.property.ComponentProperty;
import org.aksw.autosparql.cube.property.scorer.ScoreResult;
import de.konradhoeffner.commons.Pair;
import edu.stanford.nlp.util.Comparators;

@EqualsAndHashCode
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
		score = Math.max(Collections.max(nameRefs.values()),
				valueRefs.values().stream().mapToDouble(ScoreResult::getScore).max().orElse(0));
	}

}