package org.aksw.autosparql.cube.detector;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import lombok.Data;
import org.aksw.autosparql.cube.Cube;
import org.aksw.autosparql.cube.property.scorer.ScoreResult;
import org.aksw.autosparql.cube.restriction.IntervalRestriction;
import org.aksw.autosparql.cube.restriction.RestrictionWithPhrase;
import org.aksw.autosparql.cube.restriction.TopRestriction;
import org.aksw.autosparql.cube.restriction.TopRestriction.OrderModifier;
import org.apache.lucene.analysis.core.StopAnalyzer;

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

	public Optional<RestrictionWithPhrase> detect(Cube cube, String phrase)
	{
		phrase = removeStopwords(phrase);

		for(Entry<Pattern,OrderModifier> e: patternModifier.entrySet())
		{
			Matcher matcher = e.getKey().matcher(phrase);
			if(matcher.find())
			{
				int n = Integer.parseInt(matcher.group(1));
				String w = matcher.group(2);
				Set<ScoreResult> results = matchPart(cube, w);
				if(!results.isEmpty())
				{
					ScoreResult max = results.stream().max(Comparator.comparing(ScoreResult::getScore)).get();
					RestrictionWithPhrase restriction = null;
					// patterns don't overlap so we can return here
					return Optional.of(new TopRestriction(max.property, max.value, n,e.getValue()));
				}
			}
		}
		return Optional.empty();
	}

}