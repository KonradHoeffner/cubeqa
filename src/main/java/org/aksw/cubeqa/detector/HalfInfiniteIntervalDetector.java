package org.aksw.cubeqa.detector;

import static org.aksw.cubeqa.detector.IntervalType.*;
import java.util.*;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import lombok.*;
import lombok.extern.log4j.Log4j;
import org.aksw.cubeqa.Cube;
import org.aksw.cubeqa.Stopwords;
import org.aksw.cubeqa.property.scorer.ScoreResult;
import org.aksw.cubeqa.restriction.*;
import org.aksw.cubeqa.template.CubeTemplateFragment;

/**Detects numerical intervals with one infinite endpoint.*/
@Log4j
@AllArgsConstructor(access=AccessLevel.PRIVATE)
public class HalfInfiniteIntervalDetector extends Detector
{
	public static final HalfInfiniteIntervalDetector		INSTANCE		= new HalfInfiniteIntervalDetector();

	private static final double	MIN_SIMILARITY	= 0.3;
	final static String[][]							KEYWORDS		= new String[][] { { ">", "more than", "larger than" },
			{ ">=", "at least", "no less than" }, { "<", "less than", "smaller than" }, { "at most", "up to including" } };
	final static IntervalType[]						INTERVAL_TYPES	= { LEFT_OPEN, LEFT_CLOSED, RIGHT_OPEN, RIGHT_CLOSED };

	/** The matching group of a regular expression which contains the number (endpoint) of the interval.**/
	private static final Map<Pattern, Integer>	NUMBER_GROUP = new HashMap<>();
	/** The matching group of a regular expression which contains the phrase (unit) of the interval.**/
	private static final Map<Pattern, Integer>	PHRASE_GROUP = new HashMap<>();

	final static Map<Pattern, IntervalType>			PATTERN_TO_TYPE;

	static
	{
		Map<Pattern, IntervalType> patternToType = new HashMap<>();
		for (int i = 0; i < KEYWORDS.length; i++)
		{
			final int ii = i;
			Arrays.stream(
					KEYWORDS[ii]).map(
							HalfInfiniteIntervalDetector::patterns).flatMap(
									Set::stream).forEach(
											p -> patternToType.put(
													p,
													INTERVAL_TYPES[ii]));
		}
		PATTERN_TO_TYPE = Collections.unmodifiableMap(patternToType);
	}

	private static Set<Pattern> patterns(String keyword)
	{
		Set<Pattern> patterns = new HashSet<Pattern>();
		{
		Pattern p = Pattern.compile(
				keyword + "\\s+(\\d+)\\s+" + PHRASE_REGEX,
				Pattern.CASE_INSENSITIVE);
		patterns.add(p);
		NUMBER_GROUP.put(p, 1);
		PHRASE_GROUP.put(p, 2);
		}
		{
		Pattern p = Pattern.compile(
				PHRASE_REGEX + "\\s+(of )?" + keyword + "\\s+(\\d+)",
				Pattern.CASE_INSENSITIVE);
		patterns.add(p);
		NUMBER_GROUP.put(p, 4);
		PHRASE_GROUP.put(p, 1);
		}
		{
		Pattern p = Pattern.compile(
				keyword + "\\s+(\\d+)\\s+" + WORD_REGEX,
				Pattern.CASE_INSENSITIVE);
		patterns.add(p);
		NUMBER_GROUP.put(p, 1);
		PHRASE_GROUP.put(p, 2);
		}
		{
		Pattern p = Pattern.compile(
				WORD_REGEX + "\\s+(of )?" + keyword + "\\s+(\\d+)",
				Pattern.CASE_INSENSITIVE);
		patterns.add(p);
		NUMBER_GROUP.put(p, 3);
		PHRASE_GROUP.put(p, 1);
		}
		return patterns;
	}

	@RequiredArgsConstructor
	class ScoredRestriction
	{
		public final Restriction restriction;
		public final double score;
		public final String phrase;
		public final int matchBegin;
		public final int matchEnd;
	}

	public Set<CubeTemplateFragment> detect(Cube cube, String phrase)
	{
		Set<CubeTemplateFragment> fragments = new HashSet<>();
//		phrase = Stopwords.remove(phrase,Stopwords.STOPWORDS);
		Set<ScoredRestriction> srs = new HashSet<>();

		for (Entry<Pattern, IntervalType> e : PATTERN_TO_TYPE.entrySet())
		{
			Matcher matcher = e.getKey().matcher(
					phrase);
			while (matcher.find())
			{
				Pattern pattern = e.getKey();
				// TODO floats also
				int n = Integer.parseInt(matcher.group(NUMBER_GROUP.get(pattern)));
				String w = matcher.group(PHRASE_GROUP.get(pattern));

				Set<ScoreResult> results = matchPart(
						cube,
						w).stream().filter(sr->sr.getScore()>=MIN_SIMILARITY).collect(Collectors.toSet());
				if (!results.isEmpty())
				{
					ScoreResult max = results.stream().max(Comparator.comparing(ScoreResult::getScore)).get();
					RestrictionWithPhrase restriction = null;

					switch (e.getValue())
					{
						case LEFT_CLOSED:
							restriction = new IntervalRestriction(max.property, max.value, n, Double.POSITIVE_INFINITY, false);
							break;
						case LEFT_OPEN:
							restriction = new IntervalRestriction(max.property, max.value, n, Double.POSITIVE_INFINITY, true);
							break;
						case RIGHT_CLOSED:
							restriction = new IntervalRestriction(max.property, max.value, Double.NEGATIVE_INFINITY, n, false);
							break;
						case RIGHT_OPEN:
							restriction = new IntervalRestriction(max.property, max.value, Double.NEGATIVE_INFINITY, n, true);
							break;
					}
// TODO what are scored restrictions good for?
					srs.add(new ScoredRestriction(restriction, max.score, matcher.group(0),matcher.start(),matcher.end()));
					CubeTemplateFragment fragment = new CubeTemplateFragment(cube, matcher.group(0));
					fragment.getRestrictions().add(restriction);
					fragments.add(fragment);
					phrase = phrase.replace(matcher.group(0), "").replace("  "," ");
					log.debug("detected restriction "+restriction+" in phrase "+matcher.group(0));
				}
			}
		}
		// TODO get overlap and throw out low score ones

		return fragments;
	}
}