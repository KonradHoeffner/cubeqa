package org.aksw.cubeqa.template;

import static org.aksw.cubeqa.Trees.phrase;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.aksw.cubeqa.Cube;
import org.aksw.cubeqa.Stopwords;
import org.aksw.cubeqa.detector.Detector;
import org.aksw.cubeqa.property.ComponentProperty;
import org.aksw.cubeqa.property.scorer.ScoreResult;
import org.aksw.cubeqa.property.scorer.Scorers;
import org.aksw.cubeqa.restriction.RestrictionWithPhrase;
import edu.stanford.nlp.trees.Tree;

/** Generates the Cube Template. */
@RequiredArgsConstructor
@Log4j
public class CubeTemplator
{
	private static final int	PHRASE_MIN_LENGTH	= 3;
	private static final int	PHRASE_MAX_LENGTH	= 30;

	private final Cube cube;
	//	private final String question;


	public CubeTemplate buildTemplate(String question)
	{
		String noStop = Stopwords.remove(question, Stopwords.QUESTION_WORDS);
		if(!question.equals(noStop)) {log.info("removed stop words, result: "+noStop);}
		Tree root = StanfordNlp.parse(noStop);
		return visitRecursive(root).toTemplate();
	}

	List<CubeTemplateFragment> fragments(List<Tree> trees, Predicate<CubeTemplateFragment> predicate)
	{
		return trees.stream()
				.map(this::visitRecursive)
				.filter(predicate)
				.collect(Collectors.toList());
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
		MatchResult result = null;
		CubeTemplateFragment detectedFragment = null;
		CubeTemplateFragment undetectedFragment = null;

		if(phrase.length()>PHRASE_MAX_LENGTH)
		{
			log.trace("phrase more than "+PHRASE_MAX_LENGTH+" characters, going deeper");
		} else
		{
			log.trace("visiting tree "+tree);
			log.trace("Phrase \""+phrase+"\"...");

			for(Detector detector: Detector.DETECTORS)
			{
				Optional<RestrictionWithPhrase> restriction = detector.detect(cube,phrase);
				if(restriction.isPresent())
				{
					detectedFragment = new CubeTemplateFragment(cube, restriction.get().phrase);
					detectedFragment.restrictions.add(restriction.get());
					if(restriction.get().phrase.equals(phrase))
					{
						log.trace("Whole phrase matched by detector, finished with this phrase.");
						return detectedFragment;
					}
					phrase = phrase.replace(detectedFragment.phrase,"");
					log.debug("Detector matched part: '"+detectedFragment.phrase+"', left over phrase: "+phrase);
					if(phrase.length()<PHRASE_MIN_LENGTH)
					{
						log.trace("left over phrase less than "+PHRASE_MIN_LENGTH+" characters, skipped: "+phrase);
						return detectedFragment;
					}
					break;
				}
			}
			// either we detected nothing or only part of the phrase
			result = identify(phrase);
		}
		if(result!=null&&!result.isEmpty())
		{
			// whole phrase matched, subtrees skipped
			log.trace("matched to "+result);
			undetectedFragment = result.toFragment(cube);
		}
		else
		{
			// can't match the whole phrase, match subtrees separately
			log.trace("unmatched, looking at subtrees");
			List<CubeTemplateFragment> childFragments = fragments(tree.getChildrenAsList(),x->true);
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
						log.trace("unmatched fragment combination do not match anything, thrown away");
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
				undetectedFragment = new CubeTemplateFragment(cube,phrase);
			} else
			{
				undetectedFragment = CubeTemplateFragment.combine(usefulChildFragments);
			}
		}

		if(detectedFragment==null)
		{
			return undetectedFragment;
		} else
		{
			return CubeTemplateFragment.combine(Arrays.asList(detectedFragment,undetectedFragment));
		}
	}

	public MatchResult identify(String phrase)
	{
		Map<ComponentProperty,Double> nameRefs = Scorers.scorePhraseProperties(cube,phrase);
		Map<ComponentProperty,ScoreResult> valueRefs = Scorers.scorePhraseValues(cube,phrase);
		return new MatchResult(phrase, nameRefs, valueRefs);
	}

}