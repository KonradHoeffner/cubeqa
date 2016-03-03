package org.aksw.cubeqa.detector;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.aksw.cubeqa.Config;
import org.aksw.cubeqa.Cube;
import org.aksw.cubeqa.property.ComponentProperty;
import org.aksw.cubeqa.restriction.UriRestriction;
import org.aksw.cubeqa.restriction.ValueRestriction;
import org.aksw.cubeqa.template.Fragment;
import com.hp.hpl.jena.vocabulary.XSD;

/** Detects phrases like "in Yemen" or "in the City of Washington". **/
public enum InPlaceDetector implements Detector
{
	INSTANCE;

	String regex = "in(?: the)?((?: [A-Z][a-z]+)*(?: of)?(?: [A-Z][a-z]+)*)"; // proper noun sequence
	Pattern pattern = Pattern.compile(regex);

	@Override public Set<Fragment> detect(final Cube cube, final String phrase)
	{
		Set<Fragment> fragments = new HashSet<>();
		String restPhrase = phrase;
		// can be either a string or an object property
		List<ComponentProperty> placeProperties = cube.properties.values().stream()
				.filter(p->p.range==null||p.range.equals(XSD.xstring.getURI())||!p.range.startsWith(XSD.getURI())).collect(Collectors.toList());
		if(!placeProperties.isEmpty())
		{		
			Matcher matcher;
			while((matcher = pattern.matcher(restPhrase)).find())
			{
				Matcher finalMatcher = matcher;
				restPhrase = phrase.replace(matcher.group(0), " ").replaceAll("\\s+"," ");
				String place = matcher.group(1);				
				for(ComponentProperty p: placeProperties)
				{
					p.scorer.score(place).ifPresent(scoreResult->{
						if(scoreResult.score>=Config.INSTANCE.placeMinScore)
						{
							Fragment fragment =  new Fragment(cube, finalMatcher.group(0));
							if(scoreResult.value.startsWith("http")) // TODO: implement more elegantly
							{
								fragment.getRestrictions().add(new UriRestriction(p,scoreResult.value));
							} else
							{
								fragment.getRestrictions().add(new ValueRestriction(p,scoreResult.value));
							}
							fragments.add(fragment);
							//						break;
						}
					});
				}
			}
		}
		//		fragments.add(new Fragment(cube, restPhrase));
		return fragments;
	}

}