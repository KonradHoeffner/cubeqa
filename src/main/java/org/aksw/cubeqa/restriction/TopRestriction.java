package org.aksw.cubeqa.restriction;

import java.util.Collections;
import java.util.Set;
import lombok.EqualsAndHashCode;
import org.aksw.cubeqa.property.ComponentProperty;

/** Restriction for top n or bottom n items. */
@EqualsAndHashCode(callSuper=true)
public class TopRestriction extends RestrictionWithPhrase
{
	public enum OrderModifier {ASC,DESC};
	final int n;
	final OrderModifier modifier;

	@Override public Set<String> orderLimitPatterns()
	{
//		System.out.println(property.var);
//		System.out.println(property.cube.properties.ge);
		return Collections.singleton("order by "+modifier.toString()+"(xsd:decimal(?"+property.var+")) limit "+n);
	}

	public TopRestriction(ComponentProperty property,String phrase, int n, OrderModifier modifier)
	{
		super(property, phrase);
		this.n=n;
		this.modifier=modifier;
	}
}