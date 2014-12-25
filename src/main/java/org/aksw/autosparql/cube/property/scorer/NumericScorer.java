package org.aksw.autosparql.cube.property.scorer;

import java.util.Arrays;
import org.aksw.autosparql.cube.CubeSparql;
import org.aksw.autosparql.cube.property.ComponentProperty;
import com.google.common.collect.Range;
import com.google.common.primitives.Floats;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;

/** Scores numbers both based on proximity to nearest property value and on count. */
public class NumericScorer extends Scorer
{
	final Range<Double> range;

	public NumericScorer(ComponentProperty property)
	{
		super(property);
		String query = "select (min(xsd:double(?d)) as ?min) (max(xsd:double(?d)) as ?max) {?o a qb:Observation. ?o qb:dataSet <"+property.cube.uri+">."
				+ "?o <"+property.uri+"> ?d.}";
		QuerySolution qs = property.cube.sparql.select(query).next();
		range = Range.closed(qs.get("min").asLiteral().getDouble(), qs.get("max").asLiteral().getDouble());
	}

	@Override public double unsafeScore(String value)
	{
		double d = Double.valueOf(value);
		return range.contains(d)?1:0;
	}
}