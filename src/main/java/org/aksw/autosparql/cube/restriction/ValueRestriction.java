package org.aksw.autosparql.cube.restriction;

import java.util.Collections;
import java.util.Set;
import org.aksw.autosparql.cube.property.ComponentProperty;
import com.hp.hpl.jena.vocabulary.XSD;

/** restriction on a literal value **/
public class ValueRestriction extends Restriction
{
	final String value;

	public Set<String> wherePatterns()
	{
		// TODO: add datatypes from range or somewhere else
		String pattern;
		pattern = OBS_VAR+" <"+property+"> "+uniqueVar+". filter(str("+uniqueVar+")=\""+value+"\")";
		if(property.range!=null)
		{
			String uri = property.range;
			if(uri.startsWith(XSD.getURI()))
			{
				pattern = OBS_VAR+" <"+property+"> \""+value+"\"^^<"+uri+">. )";
			};
		}

		return Collections.singleton(pattern);
//		String literal = "\""+value+"\"";
//		return Collections.singleton("?obs <"+property+"> \""+literal+"\"");
	}

	public ValueRestriction(ComponentProperty property, String value)
	{
		super(property);
		this.value = value;
	}

}