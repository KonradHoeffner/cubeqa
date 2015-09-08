package org.aksw.cubeqa.restriction;

import java.util.Collections;
import java.util.Set;
import org.aksw.cubeqa.property.ComponentProperty;
import lombok.EqualsAndHashCode;

/** Restriction for top n or bottom n items. */
@EqualsAndHashCode(callSuper=true)
public class TopRestriction extends Restriction
{
	public enum OrderModifier {ASC,DESC};
	final int n;
	final OrderModifier modifier;

	@Override public Set<String> orderLimitPatterns()
	{
		return Collections.singleton("order by "+modifier.toString()+"(xsd:decimal(?"+property.var+")) limit "+n);
	}

	public TopRestriction(ComponentProperty property, int n, OrderModifier modifier)
	{
		super(property);
		this.n=n;
		this.modifier=modifier;
	}
}