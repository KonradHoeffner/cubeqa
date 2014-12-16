package org.aksw.autosparql.cube.property.scorer;

import org.aksw.autosparql.cube.Cube;
import org.aksw.autosparql.cube.property.ComponentProperty;

public class UriScorer extends StringScorer
{

	public UriScorer(Cube cube, ComponentProperty property)
	{
		super(cube,property);
	}

	public double score(String value)
	{
		double c = values.count(value);
		if(c==0) return 0;
		// +1 to prevent div by 0 the nearer the score to the max, the higher the value, but don't fall of too steep so use log.
		return Math.log(c+1)/Math.log(maxCount+1);
	}
}