package org.aksw.autosparql.cube.restriction;

import java.util.Collections;
import java.util.Set;
import lombok.EqualsAndHashCode;
import org.aksw.autosparql.cube.property.ComponentProperty;

/** top n items */
@EqualsAndHashCode(callSuper=true)
public class TopRestriction extends Restriction
{
	public enum Modifier {ASC,DESC};
	final int n;
	final Modifier modifier;

	@Override public Set<String> orderLimitPatterns()
	{
		return Collections.singleton("order by "+modifier.toString()+"(?"+property.var+") limit "+n);
	}


	public TopRestriction(ComponentProperty property, int n, Modifier modifier)
	{
		super(property);
		this.n=n;
		this.modifier=modifier;
	}

}