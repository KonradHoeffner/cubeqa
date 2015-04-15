package org.aksw.cubeqa.restriction;

import java.util.Collections;
import java.util.Set;
import lombok.EqualsAndHashCode;
import org.aksw.cubeqa.property.ComponentProperty;
import com.hp.hpl.jena.vocabulary.XSD;

/** Restriction on a literal value. **/
@EqualsAndHashCode(callSuper=true)
public class ValueRestriction extends Restriction
{
	final String value;

	public Set<String> wherePatterns()
	{
		// TODO: add datatypes from range or somewhere else
		String pattern;
		String range = property.range;
		if(range==null||!(range.startsWith(XSD.getURI())))
		{
			pattern = OBS_VAR+" <"+property+"> "+uniqueVar+".\nfilter(str("+uniqueVar+")=\""+value+"\").";
		} else
			if(range.equals(XSD.gYear.getURI()))
			{
				if(!value.matches("[0-9]+")) throw new RuntimeException(value+" is not a valid year");
				pattern = OBS_VAR+" <"+property+"> "+uniqueVar+".\nfilter(year("+uniqueVar+")="+value+").";
			}
			else
			if(range.equals(XSD.xstring.getURI()))
			{
				pattern = OBS_VAR+" <"+property+"> \""+value+"\".";
			} else
			{
				pattern = OBS_VAR+" <"+property+"> \""+value+"\"^^<"+range+">.";
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