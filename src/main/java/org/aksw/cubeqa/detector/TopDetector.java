package org.aksw.cubeqa.detector;

import java.util.*;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.aksw.cubeqa.Cube;
import org.aksw.cubeqa.property.scorer.ScoreResult;
import org.aksw.cubeqa.restriction.RestrictionWithPhrase;
import org.aksw.cubeqa.restriction.TopRestriction;
import org.aksw.cubeqa.restriction.TopRestriction.OrderModifier;
import org.aksw.cubeqa.template.CubeTemplateFragment;
import lombok.Data;

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

	final Map<Pattern,OrderModifier> numberPatternModifier = new HashMap<>();
	final Map<Pattern,OrderModifier> noNumberPatternModifier = new HashMap<>();

	private static Set<Pattern> numberPatterns(String keyword)
	{
		Set<Pattern> patterns = new HashSet<Pattern>();
		final String PHRASE = "(\\w+(\\s\\w+)*)";
		//		patterns.add(Pattern.compile("([+-]?\\d+([\\.,]\\d+)?)\\s+"+keyword+"\\s+(\\w+)"));
		patterns.add(Pattern.compile("(\\d+)\\s+"+keyword+"\\s+"+PHRASE,Pattern.CASE_INSENSITIVE));
		patterns.add(Pattern.compile(keyword+"\\s+(\\d+)\\s+"+PHRASE,Pattern.CASE_INSENSITIVE));
		return patterns;
	}

	private static Pattern noNumberPattern(String keyword)
	{
		Set<Pattern> patterns = new HashSet<Pattern>();
		final String PHRASE = "(\\w+(\\s\\w+)*)";
		return Pattern.compile("[\\s,]+"+keyword+"[\\s,]+"+PHRASE,Pattern.CASE_INSENSITIVE);
	}

	private TopDetector()
	{
		Arrays.stream(keywords[0]).map(TopDetector::numberPatterns).flatMap(Set::stream).forEach(p->numberPatternModifier.put(p,OrderModifier.DESC));
		Arrays.stream(keywords[1]).map(TopDetector::numberPatterns).flatMap(Set::stream).forEach(p->numberPatternModifier.put(p,OrderModifier.ASC));
		Arrays.stream(keywords[0]).map(TopDetector::noNumberPattern).forEach(p->noNumberPatternModifier.put(p,OrderModifier.DESC));
		Arrays.stream(keywords[1]).map(TopDetector::noNumberPattern).forEach(p->noNumberPatternModifier.put(p,OrderModifier.ASC));
	}

	@Override public Set<CubeTemplateFragment> detect(final Cube cube, final String phrase)
	{
		Set<CubeTemplateFragment> fragments = new HashSet<>();
		String restPhrase = phrase;
		// with numbers first as searching without numbers first would discard the numbers of the numbered ones
		for(Entry<Pattern,OrderModifier> e: numberPatternModifier.entrySet())
		{
			Matcher matcher = e.getKey().matcher(restPhrase);
			while(matcher.find())
			{
				restPhrase = phrase.replace(matcher.group(0), " ").replaceAll("\\s+"," ");
				int n = Integer.parseInt(matcher.group(1));
				String w = matcher.group(2);
				Set<ScoreResult> results = matchPart(cube, w);
				if(results.isEmpty()) // unknown property, use default answer property
				{
					CubeTemplateFragment fragment =  new CubeTemplateFragment(cube, matcher.group(0).replace(w, ""));
					fragment.getRestrictions().add(new TopRestriction(cube.getDefaultAnswerProperty(),n,e.getValue()));
					fragments.add(fragment);
				} else
				{
					ScoreResult max = results.stream().max(Comparator.comparing(ScoreResult::getScore)).get();
					RestrictionWithPhrase restriction = null;

					CubeTemplateFragment fragment =  new CubeTemplateFragment(cube, matcher.group(0));
					fragment.getRestrictions().add(new TopRestriction(max.property,n,e.getValue()));
					fragments.add(fragment);
					// TODO: check this function for the top 10 aided countries case
					// TODO: make sure each part is matched only once
				}
			}
		}
		for(Entry<Pattern,OrderModifier> e: noNumberPatternModifier.entrySet())
		{
			Matcher matcher = e.getKey().matcher(restPhrase);
			while(matcher.find())
			{
				restPhrase = phrase.replace(matcher.group(0), " ").replaceAll("\\s+"," ");
				String w = matcher.group(1);
				Set<ScoreResult> results = matchPart(cube, w);
				if(results.isEmpty()) // unknown property, use default answer property
				{
					CubeTemplateFragment fragment =  new CubeTemplateFragment(cube, matcher.group(0).replace(w, ""));
					fragment.getRestrictions().add(new TopRestriction(cube.getDefaultAnswerProperty(),1,e.getValue()));
					fragments.add(fragment);
				} else
				{
					ScoreResult max = results.stream().max(Comparator.comparing(ScoreResult::getScore)).get();
					RestrictionWithPhrase restriction = null;

					CubeTemplateFragment fragment =  new CubeTemplateFragment(cube, matcher.group(0));
					fragment.getRestrictions().add(new TopRestriction(max.property,1,e.getValue()));
					fragments.add(fragment);
				}
			}
		}
		return fragments;
	}

}