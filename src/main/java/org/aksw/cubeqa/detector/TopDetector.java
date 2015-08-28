package org.aksw.cubeqa.detector;

import java.util.*;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.Data;
import org.aksw.cubeqa.Cube;
import org.aksw.cubeqa.Stopwords;
import org.aksw.cubeqa.property.scorer.ScoreResult;
import org.aksw.cubeqa.restriction.*;
import org.aksw.cubeqa.restriction.TopRestriction.OrderModifier;
import org.aksw.cubeqa.template.CubeTemplateFragment;

/** Detects "highest n" or "lowest n" type phrases.
 *  Patterns: [keyword] [n] [measure] [dimension value]
 *  Example: [Top] [10] [aid receiving] [geographic areas]
 * */

public class TopDetector extends Detector
{
	@Data
	public static class TopDetectorResult
	{
		final OrderModifier modifier;
		final int n;
		final String ref;
	}

	final String[][] keywords = new String[][] {{"highest number","highest amount","top","most","highest","largest","biggest"},{"least","smallest","lowest"}};
//	final String[][] keywords = new String[][] {{"highest"},{}};
	public static final TopDetector INSTANCE = new TopDetector();

	final Map<Pattern,OrderModifier> patternModifier = new HashMap<>();

	private static Set<Pattern> pattern(String keyword)
	{
		Set<Pattern> patterns = new HashSet<Pattern>();
		final String PHRASE = "(\\w+(\\s\\w+)*)";
		//		patterns.add(Pattern.compile("([+-]?\\d+([\\.,]\\d+)?)\\s+"+keyword+"\\s+(\\w+)"));
		patterns.add(Pattern.compile("(\\d+)\\s+"+keyword+"\\s+"+PHRASE,Pattern.CASE_INSENSITIVE));
		patterns.add(Pattern.compile(keyword+"\\s+(\\d+)\\s+"+PHRASE,Pattern.CASE_INSENSITIVE));
		return patterns;
	}

	private TopDetector()
	{
		Arrays.stream(keywords[0]).map(TopDetector::pattern).flatMap(Set::stream).forEach(p->patternModifier.put(p,OrderModifier.DESC));
		Arrays.stream(keywords[1]).map(TopDetector::pattern).flatMap(Set::stream).forEach(p->patternModifier.put(p,OrderModifier.ASC));
	}

	public Set<CubeTemplateFragment> detect(Cube cube, String phrase)
	{
		Set<CubeTemplateFragment> fragments = new HashSet<>();
		for(Entry<Pattern,OrderModifier> e: patternModifier.entrySet())
		{
			Matcher matcher = e.getKey().matcher(phrase);
			while(matcher.find())
			{
				int n = Integer.parseInt(matcher.group(1));
				String w = matcher.group(2);
				Set<ScoreResult> results = matchPart(cube, w);
				if(!results.isEmpty())
				{
					ScoreResult max = results.stream().max(Comparator.comparing(ScoreResult::getScore)).get();
					RestrictionWithPhrase restriction = null;
					// patterns don't overlap so we can return here
					CubeTemplateFragment fragment =  new CubeTemplateFragment(cube, matcher.group(0));
					fragment.getRestrictions().add(new TopRestriction(max.property, max.value, n,e.getValue()));

					// TODO: check this function for the top 10 aided countries case
				}
			}
		}
		return fragments;
	}

}