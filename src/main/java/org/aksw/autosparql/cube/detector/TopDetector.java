package org.aksw.autosparql.cube.detector;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Pattern;
import org.aksw.autosparql.cube.restriction.TopRestriction.Modifier;

public class TopDetector
{
	final String[][] keywords = new String[][] {{"top","most","highest","largest","biggest"},{"least","smallest","lowest"}};
	public static final TopDetector INSTANCE = new TopDetector();

	final Map<Pattern,Modifier> patternModifier = new HashMap<>();

	private static Set<Pattern> pattern(String keyword)
	{
		Set<Pattern> patterns = new HashSet<Pattern>();
		patterns.add(Pattern.compile("(\\d+)\\s+"+keyword+"\\s+(\\w+)"));
		patterns.add(Pattern.compile(keyword+"\\s+(\\d+)\\s+(\\w+)"));
		return patterns;
	}

	private TopDetector()
	{
		Arrays.stream(keywords[0]).map(TopDetector::pattern).flatMap(Set::stream).forEach(p->patternModifier.put(p,Modifier.ASC));
		Arrays.stream(keywords[1]).map(TopDetector::pattern).flatMap(Set::stream).forEach(p->patternModifier.put(p,Modifier.DESC));
	}

	Modifier detect(String phrase)
	{
		for(Entry<Pattern,Modifier> e: patternModifier.entrySet())
		{
			if(e.getKey().matcher(phrase).find()) return e.getValue();
		}
		return null;
	}

}