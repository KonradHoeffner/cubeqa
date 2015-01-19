package org.aksw.autosparql.cube.restriction;

import org.aksw.autosparql.cube.property.ComponentProperty;

public class RestrictionWithPhrase extends Restriction
{
	public final String phrase;

	public RestrictionWithPhrase(ComponentProperty property, String phrase)
	{
		super(property);
		this.phrase = phrase;
	}
}
