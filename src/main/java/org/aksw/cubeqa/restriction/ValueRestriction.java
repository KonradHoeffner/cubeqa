package org.aksw.cubeqa.restriction;

import java.util.Collections;
import java.util.Set;
import org.aksw.cubeqa.property.ComponentProperty;
import com.hp.hpl.jena.vocabulary.XSD;
import lombok.EqualsAndHashCode;
import lombok.extern.log4j.Log4j;

/** Restriction on a literal value. **/
@EqualsAndHashCode(callSuper=true)
@Log4j
public class ValueRestriction extends Restriction
{
	final String value;

	@Override public Set<String> wherePatterns()
	{
		// TODO: add datatypes from range or somewhere else
		String pattern;
		String range = property.range;
		if(range==null||!(range.startsWith(XSD.getURI())))
		{
			pattern = OBS_VAR+" <"+property.uri+"> "+uniqueVar+".\nfilter(str("+uniqueVar+")=\""+value+"\").";
		} else
			if(range.equals(XSD.gYear.getURI()))
			{
				if(!value.matches("[0-9]+"))
				{
					//TODO readd exception
					//						throw new RuntimeException("'"+value+"' is not a valid year");
					log.fatal("'"+value+"' is not a valid year");
					return Collections.emptySet();
				}
				pattern = OBS_VAR+" <"+property.uri+"> "+uniqueVar+".\nfilter(year("+uniqueVar+")="+value+").";
			}
			else
				if(range.equals(XSD.xstring.getURI()))
				{
					pattern = OBS_VAR+" <"+property.uri+"> \""+value+"\".";
				} else
				{
					pattern = OBS_VAR+" <"+property.uri+"> \""+value+"\"^^<"+range+">.";
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