package org.aksw.cubeqa.detector;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.aksw.cubeqa.Config;
import org.aksw.cubeqa.Cube;
import org.aksw.cubeqa.property.ComponentProperty;
import org.aksw.cubeqa.property.scorer.ScoreResult;
import org.aksw.cubeqa.restriction.ValueRestriction;
import org.aksw.cubeqa.template.Fragment;
import org.apache.jena.vocabulary.XSD;

/** Detects phrases like "in 2009" with the year ranging from 1000 to 2999. **/
public enum InYearDetector implements Detector
{
	INSTANCE;

	String regex = "in (?:(?:the )?year (?:of )?)?([1-2][0-9]{3})";
	Pattern pattern = Pattern.compile(regex);

	@Override public Set<Fragment> detect(final Cube cube, final String phrase)
	{
		Set<Fragment> fragments = new HashSet<>();
		String restPhrase = phrase;
		List<ComponentProperty> yearProperties = cube.properties.values().stream().filter(p->XSD.gYear.getURI().equals(p.range)).collect(Collectors.toList());
		if(!yearProperties.isEmpty())
		{		
			Matcher matcher;
			while((matcher = pattern.matcher(restPhrase)).find())
			{
				restPhrase = phrase.replace(matcher.group(0), " ").replaceAll("\\s+"," ");
				String year = matcher.group(1);				
				for(ComponentProperty p: yearProperties)
				{
					Optional<ScoreResult> res = p.scorer.score(year);
					if(res.isPresent()&&res.get().score>=Config.INSTANCE.boostTemporal)
					{
						Fragment fragment =  new Fragment(cube, matcher.group(0));
						fragment.getRestrictions().add(new ValueRestriction(p,year));
						fragments.add(fragment);
						break;
					}
				}
			}
		}
//		fragments.add(new Fragment(cube, restPhrase));
		return fragments;
	}

}