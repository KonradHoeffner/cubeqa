package org.aksw.autosparql.cube.restriction;

import java.util.Collections;
import java.util.Set;
import lombok.AllArgsConstructor;

/** top n items */
@AllArgsConstructor
public class TopRestriction extends Restriction
{
	public enum Modifier {ASC,DESC};
	int n;
	Modifier modifier;

	@Override public Set<String> orderLimitPatterns()
	{
		return Collections.singleton("order by "+modifier.toString()+"(?value) limit "+n);
	}
}