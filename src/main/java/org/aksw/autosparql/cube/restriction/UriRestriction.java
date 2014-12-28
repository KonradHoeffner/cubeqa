package org.aksw.autosparql.cube.restriction;

import java.util.Collections;
import java.util.Set;
import lombok.EqualsAndHashCode;
import org.aksw.autosparql.cube.property.ComponentProperty;

/** restriction on a value from a given interval **/
@EqualsAndHashCode(callSuper=true)
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