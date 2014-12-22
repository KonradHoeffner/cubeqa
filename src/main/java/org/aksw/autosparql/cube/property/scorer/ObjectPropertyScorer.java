package org.aksw.autosparql.cube.property.scorer;

import org.aksw.autosparql.cube.property.ComponentProperty;

/** Scores object properties, which scores exclusively by count in relation to maxCount.*/
public class ObjectPropertyScorer extends DatatypePropertyScorer
{

	public ObjectPropertyScorer(ComponentProperty property)
	{
		super(property);
	}

	public double score(String value)
	{
		return countScore(values.count(value),maxCount);
	}
}