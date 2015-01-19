package org.aksw.autosparql.cube.restriction;

import java.util.Collections;
import java.util.Set;
import lombok.EqualsAndHashCode;
import org.aksw.autosparql.cube.property.ComponentProperty;

/** top n items */
@EqualsAndHashCode(callSuper=true)
public class TopRestriction extends RestrictionWithPhrase
{
	public enum OrderModifier {ASC,DESC};
	final int n;
	final OrderModifier modifier;

	@Override public Set<String> orderLimitPatterns()
	{
		return Collections.singleton("order by "+modifier.toString()+"(?"+property.var+") limit "+n);
	}


	public TopRestriction(ComponentProperty property,String phrase, int n, OrderModifier modifier)
	{
		super(property, phrase);
		this.n=n;
		this.modifier=modifier;
	}

}