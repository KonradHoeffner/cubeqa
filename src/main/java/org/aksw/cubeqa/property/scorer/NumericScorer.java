package org.aksw.cubeqa.property.scorer;

import java.util.Optional;
import org.aksw.cubeqa.Config;
import org.aksw.cubeqa.property.ComponentProperty;
import com.google.common.collect.Range;
import org.apache.jena.datatypes.DatatypeFormatException;
import org.apache.jena.query.QuerySolution;
import lombok.extern.slf4j.Slf4j;

/** tests if a number is included in the range. */
@Slf4j
public class NumericScorer extends Scorer
{
	final Range<Double> range;

	public NumericScorer(ComponentProperty property)
	{
		super(property);
		// triggers Virtuoso bug https://github.com/openlink/virtuoso-opensource/issues/354 on some versions
		String query = "select (min(xsd:double(?d)) as ?min) (max(xsd:double(?d)) as ?max) {?o a qb:Observation. ?o qb:dataSet <"+property.cube.uri+">."
				+ "?o <"+property.uri+"> ?d.}";
		QuerySolution qs = property.cube.sparql.select(query).next();
		log.trace(query);
		Range<Double> range2;
		try {range2 = Range.closed(qs.get("min").asLiteral().getDouble(), qs.get("max").asLiteral().getDouble());}
		// virtuoso bug
		catch(DatatypeFormatException e)
		{
			log.error("Virtuoso Bug for property "+property+", query:\n"+query,e);
			range2 = Range.closed(Double.MIN_VALUE, Double.MAX_VALUE);
		}
		range=range2;
	}

	@Override public Optional<ScoreResult> score(String value)
	{
		try
		{
			double d = Double.valueOf(value);
			return Optional.of(new ScoreResult(property, value, range.contains(d)?Config.INSTANCE.boostNumeric:0.0));
		}
		catch(NumberFormatException e) {return Optional.empty();}
	}
}