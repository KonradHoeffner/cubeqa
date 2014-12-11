package org.aksw.autosparql.cube.restriction;

import java.util.Collections;
import java.util.Set;

/** restriction on a value from a given interval **/
public class ValueRestriction extends Restriction
{
	String value;

	public Set<String> wherePatterns()
	{
		return Collections.singleton("?obs <"+property+"> \""+value+"\"");
	}

}