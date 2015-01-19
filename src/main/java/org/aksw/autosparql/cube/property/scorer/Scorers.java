package org.aksw.autosparql.cube.property.scorer;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.aksw.autosparql.cube.Cube;
import org.aksw.autosparql.cube.property.ComponentProperty;
import de.konradhoeffner.commons.Pair;

/** This class consists exclusively of static methods that operate on scorers. **/
public class Scorers
{
	private static final double	THRESHOLD	= 0.3;

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
				.filter(s->s.score>THRESHOLD)
				.collect(Collectors.toMap(result->result.property, result->result));
	}

	public static Map<ComponentProperty, Double> scorePhraseProperties(Cube cube, String phrase)
	{
		return
				cube.properties.values().stream()
				.map(p->new Pair<ComponentProperty,Double>(p, p.match(phrase)))
				.filter(p->p.b>THRESHOLD)
				.collect(Collectors.toMap(p->p.a, p->p.b));
	}
}