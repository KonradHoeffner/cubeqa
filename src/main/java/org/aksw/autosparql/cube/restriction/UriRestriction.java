package org.aksw.autosparql.cube.restriction;

import java.util.Collections;
import java.util.Set;
import org.aksw.autosparql.cube.ComponentProperty;

/** restriction on a value from a given interval **/
public class UriRestriction extends Restriction
{
	String uri;

	public Set<String> wherePatterns()
	{
		return Collections.singleton("?obs <"+property+"> <"+uri+">.");
	}

	public UriRestriction(ComponentProperty property, String uri)
	{
		super(property);
		this.uri=uri;
	}

}