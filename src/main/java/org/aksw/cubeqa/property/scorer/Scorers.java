package org.aksw.cubeqa.property.scorer;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.aksw.cubeqa.Config;
import org.aksw.cubeqa.Cube;
import org.aksw.cubeqa.property.ComponentProperty;
import de.konradhoeffner.commons.Pair;

/** This class consists exclusively of static methods that operate on scorers. **/
public class Scorers
{
	/** Score the given phrase with every property's scorer and returns all obtained results.
	 * For some properties the score may be 0 or below a threshold, in that case no result
	 * for that property is contained in the returned map.*/
	public static Map<ComponentProperty,ScoreResult> scorePhraseValues(Cube cube, String phrase)
	{
		return
				cube.properties.values().stream()
				.map(p->p.scorer.score(phrase))
				.filter(Optional::isPresent)
				.map(Optional::get)
//				.filter(s->s.score>THRESHOLD)
				.collect(Collectors.toMap(result->result.property, result->result));
	}

	public static Map<ComponentProperty, Double> scorePhraseProperties(Cube cube, String phrase)
	{
		return
				cube.properties.values().stream()
				.map(p->new Pair<>(p, p.match(phrase)))
				.filter(p->p.b>Config.INSTANCE.scorerPropertyNameMinScore)
				.collect(Collectors.toMap(p->p.a, p->p.b));
	}
}