package org.aksw.cubeqa.detector;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.aksw.cubeqa.*;
import org.aksw.cubeqa.property.ComponentProperty;
import org.aksw.cubeqa.property.scorer.ScoreResult;
import org.aksw.cubeqa.restriction.TopRestriction;
import org.aksw.cubeqa.restriction.ValueRestriction;
import org.aksw.cubeqa.template.Fragment;

import com.hp.hpl.jena.vocabulary.XSD;

/** Detects phrases like "in 2009" with the year ranging from 1000 to 2999. **/
public enum InYearDetector implements Detector
{
	INSTANCE;

	Pattern pattern = Pattern.compile("in ([1-2][0-9]{3})");

	@Override public Set<Fragment> detect(final Cube cube, final String phrase)
	{
		Set<Fragment> fragments = new HashSet<>();
		String restPhrase = phrase;
		Matcher matcher = pattern.matcher(phrase);
		while(matcher.find())
		{
			restPhrase = phrase.replace(matcher.group(0), " ").replaceAll("\\s+"," ");
			String year = matcher.group(1);
			cube.properties.values().stream().filter(p->p.range.equals(XSD.gYear)).forEach(p->
			{
				if(p.scorer.score(year).get().score==Config.INSTANCE.boostDate)
				{
				Fragment fragment =  new Fragment(cube, matcher.group(0));
				fragment.getRestrictions().add(new ValueRestriction(p,year));
				fragments.add(fragment);
				// TODO: test
				}
			});
		}
		fragments.add(new Fragment(cube, restPhrase));
		return fragments;
	}

}