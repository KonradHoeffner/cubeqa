package org.aksw.cubeqa.template;

import static org.aksw.cubeqa.Trees.phrase;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import de.konradhoeffner.commons.StopWatch;
import org.aksw.cubeqa.*;
import org.aksw.cubeqa.detector.Detector;
import org.aksw.cubeqa.property.ComponentProperty;
import org.aksw.cubeqa.property.scorer.ScoreResult;
import org.aksw.cubeqa.property.scorer.Scorers;
import de.konradhoeffner.commons.Pair;
import edu.stanford.nlp.trees.Tree;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/** Generates the Cube Template. */
@RequiredArgsConstructor
@Slf4j
public class Templator
{
	//	{log.setLevel(Level.ALL);}
	private static final int	PHRASE_MIN_LENGTH	= 3;
	private static final int	PHRASE_MAX_LENGTH	= 30;

	private final Cube cube;

	/** Sublist of trees that satisfiy the given predicate */
	List<Fragment> fragments(List<Tree> trees, Predicate<Fragment> predicate)
	{
		return trees.stream()
				.map(this::visitRecursive)
				.filter(predicate)
				.collect(Collectors.toList());
	}

	public Template buildTemplate(String question)
	{
		String replaced = Replacer.replace(question);
		if(!replaced.equals(question))
		{
			question=replaced;
			log.info("Replacement: "+question);
		}
		StopWatch eatWatch = StopWatches.INSTANCE.getWatch("eat");
		eatWatch.start();
		Optional<Pair<String,EnumSet<AnswerType>>> oPair = AnswerType.eatAndQuestionWord(question);
		eatWatch.stop();
		EnumSet<AnswerType> eats = EnumSet.allOf(AnswerType.class);
		if(!oPair.isPresent()) {log.warn("no question word found for question '"+question+"': no answer type restriction possible.");}
		else
		{
			String questionWord = oPair.get().a;
			eats = oPair.get().b;
			question = question.substring(questionWord.length());
		}

		String noStop = question;
		if(Config.INSTANCE.removeStopWords)
		{
			//			noStop = Stopwords.remove(noStop, Stopwords.FINLAND_AID_WORDS);
			noStop = Stopwords.remove(noStop, Stopwords.PROPERTY_WORDS);
		}
		if(!question.equals(noStop)) {log.info("removed stop words, result: "+noStop);}
		StopWatch detectWatch = StopWatches.INSTANCE.getWatch("detect");
		detectWatch.start();
		Pair<Fragment,String> detectResult = detect(noStop);
		detectWatch.stop();
		StopWatch parseWatch = StopWatches.INSTANCE.getWatch("parse");
		parseWatch.start();
		Tree root = StanfordNlp.parse(detectResult.b);
		parseWatch.stop();
		Fragment rootFragment = visitRecursive(root);
		Template finalTemplate = Fragment.combine(Arrays.asList(rootFragment,detectResult.a)).toTemplate(eats).get();
		return finalTemplate;
	}

	/** @param question the full question used on all detectors
	 * @return the combined detected fragment and the leftover phrase
	 */
	Pair<Fragment,String> detect(final String question)
	{
		Fragment allDetectorFragment = null;

		String reducedPhrase = question;
		for(Detector detector: Detector.DETECTORS)
		{
			Set<Fragment> detectorResults = detector.detect(cube,reducedPhrase);
			if(!detectorResults.isEmpty())
			{
				for(Fragment fragment: detectorResults)
				{
					reducedPhrase = question.replace(fragment.phrase,"").replace("  ", " ");
					if(reducedPhrase.equals(question)) {
						throw new IllegalArgumentException("fragment phrase '"+fragment.phrase+"' not found in whole phrase "+question);
					}
					log.debug("Detector "+detector.getClass().getSimpleName()+" matched part: '"+fragment.phrase+"', left over phrase: "+question);
				}
				// keep results from earlier used detectors
				if(allDetectorFragment!=null) {detectorResults.add(allDetectorFragment);}
				allDetectorFragment = Fragment.combine(new ArrayList<>(detectorResults));
			}
		}
		if(allDetectorFragment==null) {return new Pair<>(new Fragment(cube,""),reducedPhrase);}
		return new Pair<>(allDetectorFragment,reducedPhrase);
	}

	/** The recursive algorithm. */
	Fragment visitRecursive(Tree tree)
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
			return new Fragment(cube, phrase);
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

			Match matchResult = identify(phrase);
			// whole phrase matched, subtrees skipped
			if(!matchResult.isEmpty())
			{
				log.trace("matched to "+matchResult);
				return matchResult.toFragment(cube);
			}
		}
		// either we didn't match because the phrase is too long or matching didn't find anything, so match subtrees separately
		log.trace("unmatched, looking at subtrees");
		List<Fragment> childFragments = fragments(tree.getChildrenAsList(),x->true);
		if(childFragments.isEmpty()) {
			return new Fragment(cube, phrase);
		}
		List<Fragment> childFragmentsWithRefs = childFragments.stream().filter(f->!f.isEmpty()).collect(Collectors.toList());
		List<Fragment> childFragmentsWithoutRefs = new LinkedList<>(childFragments);
		childFragmentsWithoutRefs.removeAll(childFragmentsWithRefs);

		List<Fragment> usefulChildFragments = new ArrayList<>(childFragmentsWithRefs);
		// we could throw unmatched fragments away but we try to combine them into something useful first
		if(!childFragmentsWithoutRefs.isEmpty())
		{
			String childFragmentsWithoutRefsPhrase = Fragment.combine(childFragmentsWithoutRefs).phrase;
			// too small, throw away
			if(childFragmentsWithoutRefsPhrase.length()<3)
			{
				log.trace("unmatched fragment \""+childFragmentsWithoutRefsPhrase+"\" length < 3, skipped");
			}
			// it's not small, but is it useful? do all the unmatched fragments match something?
			else
			{
				// TODO check partial combinations too
				Match unmatchedResult = identify(childFragmentsWithoutRefsPhrase);
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
			return new Fragment(cube,phrase);
		} else
		{
			return Fragment.combine(usefulChildFragments);
		}
	}

	public Match identify(String phrase/*, int phraseIndex*/)
	{
		StopWatch scoreWatch = StopWatches.INSTANCE.getWatch("score");
		scoreWatch.start();
		Map<ComponentProperty,Double> nameRefs = Scorers.scorePhraseProperties(cube,phrase);
		Map<ComponentProperty,ScoreResult> valueRefs = Scorers.scorePhraseValues(cube,phrase);
		scoreWatch.stop();
		return new Match(phrase,/* phraseIndex, */nameRefs, valueRefs);
	}

}