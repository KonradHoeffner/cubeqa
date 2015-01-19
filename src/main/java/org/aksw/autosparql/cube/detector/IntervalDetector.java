package org.aksw.autosparql.cube.detector;

import static org.aksw.autosparql.cube.detector.IntervalType.*;
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
import org.aksw.autosparql.cube.restriction.TopRestriction.OrderModifier;
import org.apache.lucene.analysis.core.StopAnalyzer;

public class IntervalDetector extends Detector
{
	@Data
	public static class TopDetectorResult
	{
		final OrderModifier modifier;
		final int n;
		final String ref;
	}

	final String[][] keywords = new String[][] {{">","more than","larger than"},{">=","at least","no less than"}
	,{"<","less than","smaller than"},{"at most","up to including"}};
	final IntervalType[] INTERVAL_TYPES = {LEFT_OPEN,LEFT_CLOSED,RIGHT_OPEN,RIGHT_CLOSED};

	final Map<Pattern,IntervalType> patternModifier = new HashMap<>();
	private static final Map<Pattern,Boolean> numberFirst = new HashMap<>();

	public static final IntervalDetector INSTANCE = new IntervalDetector();


	private static Set<Pattern> pattern(String keyword)
	{
		Set<Pattern> patterns = new HashSet<Pattern>();
		Pattern p1 = Pattern.compile(keyword+"\\s+(\\d+)\\s+"+PHRASE_REGEX,Pattern.CASE_INSENSITIVE);
		patterns.add(p1);
		numberFirst.put(p1,true);
		Pattern p2 = Pattern.compile(PHRASE_REGEX+"\\s+"+keyword+"\\s+(\\d+)",Pattern.CASE_INSENSITIVE);
		patterns.add(p2);
		numberFirst.put(p2,false);
		return patterns;
	}


	private IntervalDetector()
	{
		for(int i=0;i<keywords.length;i++)
		{
			final int ii = i;
			Arrays.stream(keywords[ii]).map(IntervalDetector::pattern).flatMap(Set::stream).forEach(p->patternModifier.put(p,INTERVAL_TYPES[ii]));
		}
	}

	public Optional<RestrictionWithPhrase> detect(Cube cube, String phrase)
	{
		phrase = removeStopwords(phrase);

		for(Entry<Pattern,IntervalType> e: patternModifier.entrySet())
		{
			Matcher matcher = e.getKey().matcher(phrase);
			if(matcher.matches())
			{
				// TODO floats also
				boolean nf = numberFirst.get(e.getKey());
				int n = Integer.parseInt(matcher.group(nf?1:3));
				String w = matcher.group(nf?3:1);

				Set<ScoreResult> results = matchPart(cube, w);
				if(!results.isEmpty())
				{
					// reward longer matches
					ScoreResult max = results.stream().max(Comparator.comparing(ScoreResult::getScore)).get();
					RestrictionWithPhrase restriction = null;

					switch(e.getValue())
					{
						case LEFT_CLOSED: restriction = new IntervalRestriction(max.property, max.value, n, Double.POSITIVE_INFINITY, false);break;
						case LEFT_OPEN: restriction = new IntervalRestriction(max.property, max.value, n, Double.POSITIVE_INFINITY, true);break;
						case RIGHT_CLOSED: restriction = new IntervalRestriction(max.property, max.value, Double.NEGATIVE_INFINITY, n, false);break;
						case RIGHT_OPEN: restriction = new IntervalRestriction(max.property, max.value, Double.NEGATIVE_INFINITY, n, true);break;
					}
					// patterns don't overlap so we can return here
					return Optional.of(restriction);
				}
			}
		}
		return Optional.empty();
	}
}