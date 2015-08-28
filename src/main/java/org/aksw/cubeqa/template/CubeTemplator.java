package org.aksw.cubeqa.template;

import static org.aksw.cubeqa.Trees.phrase;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.aksw.cubeqa.*;
import org.aksw.cubeqa.detector.Aggregate;
import org.aksw.cubeqa.detector.Detector;
import org.aksw.cubeqa.property.ComponentProperty;
import org.aksw.cubeqa.property.scorer.ScoreResult;
import org.aksw.cubeqa.property.scorer.Scorers;
import org.apache.log4j.Level;
import de.konradhoeffner.commons.Pair;
import edu.stanford.nlp.trees.Tree;

/** Generates the Cube Template. */
@RequiredArgsConstructor
@Log4j
public class CubeTemplator
{
	//	{log.setLevel(Level.ALL);}
	private static final int	PHRASE_MIN_LENGTH	= 3;
	private static final int	PHRASE_MAX_LENGTH	= 30;

	private final Cube cube;

	/** Sublist of trees that satisfiy the given predicate
	 */
	List<CubeTemplateFragment> fragments(List<Tree> trees, Predicate<CubeTemplateFragment> predicate)
	{
		return trees.stream()
				.map(this::visitRecursive)
				.filter(predicate)
				.collect(Collectors.toList());
	}

	public CubeTemplate buildTemplate(String question)
	{
		String noStop = question;
		if(Config.INSTANCE.removeStopWords)
		{
			noStop = Stopwords.remove(noStop, Stopwords.FINLAND_AID_WORDS);
			noStop = Stopwords.remove(noStop, Stopwords.PROPERTY_WORDS);
		}
		if(!question.equals(noStop)) {log.info("removed stop words, result: "+noStop);}
		Pair<CubeTemplateFragment,String> detectResult = detect(noStop);
		Tree root = StanfordNlp.parse(detectResult.b);
		CubeTemplateFragment rootFragment = visitRecursive(root);
		CubeTemplate finalTemplate = CubeTemplateFragment.combine(Arrays.asList(rootFragment,detectResult.a)).toTemplate();
		// TODO move default aggregate from templator to cubetemplate or cubetemplatefragment
		Set<String> orderLimitPatterns = finalTemplate.restrictions.stream().flatMap(r->r.orderLimitPatterns().stream()).collect(Collectors.toSet());
		if(finalTemplate.aggregates.isEmpty()&&orderLimitPatterns.isEmpty()) {finalTemplate.aggregates.add(Aggregate.SUM);}
		return finalTemplate;
	}

	/** @param question
	 * @return the combined detected fragment and the leftover phrase
	 */
	Pair<CubeTemplateFragment,String> detect(String question)
	{
		CubeTemplateFragment allDetectorFragment = null;

		for(Detector detector: Detector.DETECTORS)
		{
			Set<CubeTemplateFragment> detectorResults = detector.detect(cube,question);
			if(!detectorResults.isEmpty())
			{
				for(CubeTemplateFragment fragment: detectorResults)
				{
					String reducedPhrase = question.replace(fragment.phrase,"").replace("  ", " ");
					if(reducedPhrase.equals(question)) throw new IllegalArgumentException("fragment phrase '"+fragment.phrase+"' not found in whole phrase "+question);
					question = reducedPhrase;
					log.debug("Detector "+detector.getClass().getSimpleName()+" matched part: '"+fragment.phrase+"', left over phrase: "+question);
				}
				// keep results from earlier used detectors
				if(allDetectorFragment!=null) {detectorResults.add(allDetectorFragment);}
				allDetectorFragment = CubeTemplateFragment.combine(new ArrayList<>(detectorResults));
			}
		}
		if(allDetectorFragment==null) {return new Pair<>(new CubeTemplateFragment(cube, ""),question);}
		return new Pair<>(allDetectorFragment,question);
	}

	/** The recursive algorithm. */
	CubeTemplateFragment visitRecursive(Tree tree)
	{
		while(/*!tree.isPreTerminal()&&*/tree.children().length==1)
		{
			// skipping down
			tree = tree.getChild(0);
		}
		String phrase = phrase(tree);
		if(phrase.length()<PHRASE_MIN_LENGTH)
		{
			log.trace("phrase less than "+PHRASE_MIN_LENGTH+" characters, skipped: "+phrase);
			return new CubeTemplateFragment(cube, phrase);
		}

		if(phrase.length()>PHRASE_MAX_LENGTH)
		{
			log.trace("phrase '"+phrase+"' more than "+PHRASE_MAX_LENGTH+" characters, skipping matching try");
		}
		else
		{
			log.trace("visiting tree "+tree);
			log.trace("Phrase \""+phrase+"\"...");
			// either we detected nothing or only part of the phrase
			MatchResult matchResult = identify(phrase);
			// whole phrase matched, subtrees skipped
			if(!matchResult.isEmpty())
			{
				log.trace("matched to "+matchResult);
				return matchResult.toFragment(cube);
			}
		}
		// either we didn't match because the phrase is too long or matching didn't find anything, so match subtrees separately
		log.trace("unmatched, looking at subtrees");
		List<CubeTemplateFragment> childFragments = fragments(tree.getChildrenAsList(),x->true);
		if(childFragments.isEmpty()) return new CubeTemplateFragment(cube, phrase);
		List<CubeTemplateFragment> childFragmentsWithRefs = childFragments.stream().filter(f->!f.isEmpty()).collect(Collectors.toList());
		List<CubeTemplateFragment> childFragmentsWithoutRefs = new LinkedList<>(childFragments);
		childFragmentsWithoutRefs.removeAll(childFragmentsWithRefs);

		List<CubeTemplateFragment> usefulChildFragments = new ArrayList<>(childFragmentsWithRefs);
		// we could throw unmatched fragments away but we try to combine them into something useful first
		if(!childFragmentsWithoutRefs.isEmpty())
		{
			String childFragmentsWithoutRefsPhrase = CubeTemplateFragment.combine(childFragmentsWithoutRefs).phrase;
			// too small, throw away
			if(childFragmentsWithoutRefsPhrase.length()<3)
			{
				log.trace("unmatched fragment \""+childFragmentsWithoutRefsPhrase+"\" length < 3, skipped");
			}
			// it's not small, but is it useful? do all the unmatched fragments match something?
			else
			{
				// TODO check partial combinations too
				MatchResult unmatchedResult = identify(childFragmentsWithoutRefsPhrase);
				log.trace("unmatched fragments with phrase \""+unmatchedResult.phrase+"\"");
				//				unmatchedFragments.stream().map(f->f.phrase).collect(Collectors.toList());

				if(unmatchedResult.isEmpty())
				{
					log.trace("unmatched fragment combination does not match anything.");
				} else
				{
					log.trace("unmatched fragment combination matched to "+unmatchedResult);
					usefulChildFragments.add(unmatchedResult.toFragment(cube));
				}
			}
		}
		if(usefulChildFragments.isEmpty())
		{
			log.trace("no match found for phrase \"" +phrase+"\"");
			return new CubeTemplateFragment(cube,phrase);
		} else
		{
			return CubeTemplateFragment.combine(usefulChildFragments);
		}
	}

	public MatchResult identify(String phrase)
	{
		Map<ComponentProperty,Double> nameRefs = Scorers.scorePhraseProperties(cube,phrase);
		Map<ComponentProperty,ScoreResult> valueRefs = Scorers.scorePhraseValues(cube,phrase);
//	System.out.println(">>> "+phrase);
//		if(phrase.equals("countries"))
//		{
//			System.out.println(nameRefs);
//			System.out.println(valueRefs);
//		}
		return new MatchResult(phrase, nameRefs, valueRefs);
	}

}