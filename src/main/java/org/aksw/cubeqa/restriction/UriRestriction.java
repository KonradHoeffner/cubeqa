package org.aksw.cubeqa.restriction;

import java.util.Collections;
import java.util.Set;
import lombok.EqualsAndHashCode;
import org.aksw.cubeqa.property.ComponentProperty;

/** Restriction of an object property to a specific object URI.**/
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