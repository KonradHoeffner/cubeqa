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
	private final Cube cube;
	//	private final String question;

	public CubeTemplate buildTemplate(String question)
	{
		Tree root = StanfordNlp.parse(question);
		return visitRecursive(root).toTemplate();
	}

	List<CubeTemplateFragment> fragments(List<Tree> trees, Predicate<CubeTemplateFragment> predicate)
	{
		return trees.stream()
				.map(this::visitRecursive)
				.filter(predicate)
				.collect(Collectors.toList());
	}

	CubeTemplateFragment visitRecursive(Tree tree)
	{
		while(/*!tree.isPreTerminal()&&*/tree.children().length==1)
		{
			// skipping down
			tree = tree.getChild(0);
		}
		String phrase = phrase(tree);
		if(phrase.length()<3)
		{
			log.trace("phrase less than 3 characters, skipped: "+phrase);
			return new CubeTemplateFragment(cube, phrase);
		}
		log.trace("visiting tree "+tree);
		log.trace("Phrase \""+phrase+"\"...");

		CubeTemplateFragment detectedFragment = null;
		CubeTemplateFragment undetectedFragment = null;
		for(Detector detector: Detector.DETECTORS)
		{
			Optional<RestrictionWithPhrase> restriction = detector.detect(cube,phrase);
			if(restriction.isPresent())
			{
				detectedFragment = new CubeTemplateFragment(cube, restriction.get().phrase);
				detectedFragment.restrictions.add(restriction.get());
				break;
			}
		}
		// part or all of the phrase matched with a detector
		if(detectedFragment!=null)
		{
			// whole phrase matched by detector, nothing else to do
			if(detectedFragment.phrase.equals(phrase))
			{
				log.trace("Whole phrase matched by detector, finished with this phrase.");
				return detectedFragment;
			} else
			{
				// left over phrase
				phrase = phrase.replace(detectedFragment.phrase,"");
				log.debug("Detector matched part: '"+detectedFragment.phrase+"', left over phrase: "+phrase);
				if(phrase.length()<3)
				{
					log.trace("left over phrase less than 3 characters, skipped: "+phrase);
					return detectedFragment;
				}
			}
		}
		// either we detected nothing or only part of the phrase
		MatchResult result = identify(phrase);
		if(result.isEmpty())
		{
			// can't match the whole phrase, match subtrees separately
			log.trace("unmatched, looking at subtrees");
			List<CubeTemplateFragment> fragments = fragments(tree.getChildrenAsList(),x->true);
			List<CubeTemplateFragment> matchedFragments = fragments.stream().filter(f->!f.isEmpty()).collect(Collectors.toList());
			List<CubeTemplateFragment> unmatchedFragments = new LinkedList<>(fragments);
			unmatchedFragments.removeAll(matchedFragments);

			List<CubeTemplateFragment> selectedFragments = new ArrayList<>(matchedFragments);

			if(!unmatchedFragments.isEmpty())
			{
				String unmatchedFragmentPhrase = CubeTemplateFragment.combine(unmatchedFragments).phrase;
				if(unmatchedFragmentPhrase.length()<3)
				{
					//					System.out.println("unmatched fragment \""+unmatchedFragmentPhrase+"\" length < 3, skipped");
				}
				else
				{
					MatchResult unmatchedResult = identify(unmatchedFragmentPhrase);
					log.trace("unmatched fragments with phrase \""+unmatchedResult.phrase+"\"...");
					//				unmatchedFragments.stream().map(f->f.phrase).collect(Collectors.toList());
					// can we match these leftover fragments together?

					if(unmatchedResult.isEmpty())
					{
						log.trace("unmatched");
					} else
					{
						log.trace("matched to "+unmatchedResult);
						selectedFragments.add(unmatchedResult.toFragment(cube));
					}
				}
			}
			if(selectedFragments.isEmpty())
			{
				log.trace("no match found for phrase \"" +phrase+"\"");
				undetectedFragment = new CubeTemplateFragment(cube,phrase);
			} else
			{
				undetectedFragment = CubeTemplateFragment.combine(selectedFragments);
			}
		}
		else
		{
			// whole phrase matched, subtrees skipped
			log.trace("matched to "+result);
			undetectedFragment = result.toFragment(cube);
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