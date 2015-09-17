package org.aksw.cubeqa.restriction;

import java.util.HashSet;
import java.util.Set;
import org.aksw.cubeqa.property.ComponentProperty;
import lombok.EqualsAndHashCode;

/** restriction on a value from a given interval **/

@EqualsAndHashCode(callSuper=true)
public class IntervalRestriction extends RestrictionWithPhrase
{
// TODO chose an existing interval class and intersect multiple intervals for the same property. Low priority though as I expect almost no
//	sentences to specify multiple restrictions on the same property.
	double leftEndpoint;
	double rightEndpoint;
	boolean open;

	@Override public Set<String> wherePatterns()
	{
		HashSet<String> terms = new HashSet<>();

		if(leftEndpoint>Double.NEGATIVE_INFINITY)
		{
			String leftComparator = open?">":">=";
			String leftFilter = "filter(?"+property.var+" "+leftComparator+" \""+leftEndpoint+"\"). ";
			terms.add(leftFilter);
		}
		if(rightEndpoint<Double.POSITIVE_INFINITY)
		{
			String rightComparator = open?"<":"<=";
			String rightFilter = "filter(?"+property.var+" "+rightComparator+" \""+rightEndpoint+"\"). ";
			terms.add(rightFilter);
		}
		terms.add("?obs <"+property.uri+"> ?"+property.var+".");
		return terms;
	}

	public IntervalRestriction(ComponentProperty property, String phrase, double leftEndpoint, double rightEndpoint, boolean open)
	{
		super(property,phrase);
		this.leftEndpoint = leftEndpoint;
		this.rightEndpoint = rightEndpoint;
		this.open = open;
	}

}