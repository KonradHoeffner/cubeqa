package org.aksw.autosparql.cube.detector;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import lombok.Data;
import org.aksw.autosparql.cube.detector.TopDetector.TopDetectorResult;
import org.aksw.autosparql.cube.restriction.TopRestriction.Modifier;
import org.apache.lucene.analysis.core.StopAnalyzer;

public class IntervalDetector
{

	static final Set<String> stopwords = new StopAnalyzer().getStopwordSet().stream().map(Object::toString).collect(Collectors.toSet());

	@Data
	public static class TopDetectorResult
	{
		final Modifier modifier;
		final int n;
		final String ref;
	}

	final String[][] keywords = new String[][] {{"top","most","highest","largest","biggest"},{"least","smallest","lowest"}};
	public static final TopDetector INSTANCE = new TopDetector();

	final Map<Pattern,Modifier> patternModifier = new HashMap<>();

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
		Arrays.stream(keywords[0]).map(TopDetector::pattern).flatMap(Set::stream).forEach(p->patternModifier.put(p,Modifier.DESC));
		Arrays.stream(keywords[1]).map(TopDetector::pattern).flatMap(Set::stream).forEach(p->patternModifier.put(p,Modifier.ASC));
	}

	public Optional<TopDetectorResult> detect(String phrase)
	{
		for(String stopword: stopwords) {phrase=phrase.replace(stopword, "");}

		for(Entry<Pattern,Modifier> e: patternModifier.entrySet())
		{
			Matcher matcher = e.getKey().matcher(phrase);
			if(matcher.find())
			{
				int n = Integer.parseInt(matcher.group(1));
				String w = matcher.group(2);
				return Optional.of(new TopDetectorResult(e.getValue(),n,w));
			}
		}
		return Optional.empty();
	}
}
