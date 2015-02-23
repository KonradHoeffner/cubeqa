package org.aksw.cubeqa.restriction;

import org.aksw.cubeqa.property.ComponentProperty;

public class RestrictionWithPhrase extends Restriction
{
	public final String phrase;

	public RestrictionWithPhrase(ComponentProperty property, String phrase)
	{
		super(property);
		this.phrase = phrase;
	}
}
