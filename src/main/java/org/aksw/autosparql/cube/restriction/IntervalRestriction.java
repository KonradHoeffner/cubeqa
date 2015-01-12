package org.aksw.autosparql.cube.restriction;

import java.util.HashSet;
import java.util.Set;
import org.aksw.autosparql.cube.property.ComponentProperty;
import lombok.EqualsAndHashCode;

/** restriction on a value from a given interval **/

@EqualsAndHashCode(callSuper=true)
public class IntervalRestriction extends Restriction
{
// TODO chose an existing interval class and intersect multiple intervals for the same property. Low priority though as I expect almost no
//	sentences to specify multiple restrictions on the same property.
	double leftEndpoint;
	double rightEndpoint;
	boolean open;

	public Set<String> wherePatterns()
	{
		HashSet<String> terms = new HashSet<>();

		if(leftEndpoint>Double.NEGATIVE_INFINITY)
		{
			String leftComparator = open?">":">=";
			String leftFilter = "filter(?p "+leftComparator+" \""+leftEndpoint+"\"). ";
			terms.add(leftFilter);
		}
		if(rightEndpoint<Double.POSITIVE_INFINITY)
		{
			String rightComparator = open?"<":"<=";
			String rightFilter = "filter(?p "+rightComparator+" \""+rightEndpoint+"\"). ";
			terms.add(rightFilter);
		}
		return terms;
	}


	public IntervalRestriction(ComponentProperty property, double leftEndpoint, double rightEndpoint, boolean open)
	{
		super(property);
		this.leftEndpoint = leftEndpoint;
		this.rightEndpoint = rightEndpoint;
		this.open = open;
	}

}