package org.aksw.cubeqa.template;

import edu.stanford.nlp.trees.Tree;
import lombok.extern.slf4j.Slf4j;
import org.aksw.cubeqa.Cube;
import static org.aksw.cubeqa.Trees.phrase;
import java.util.*;
import java.util.stream.Collectors;

/** Generates the Cube Template. */
@Slf4j
public class WeightedTemplator extends Templator
{
	public WeightedTemplator(Cube cube) {super(cube);}

	public Template buildTemplate(String question)
	{
		preprocess(question);
		Set<Fragment> fragments = visitRecursive(root);
		//Template finalTemplate = Fragment.combine(Arrays.asList(rootFragment, detectFragment)).toTemplate(eats).get();
		//return finalTemplate;
		return null;
	}

	protected Set<Fragment> visitRecursive(Tree tree) {visitRecursive(tree, new HashSet<Fragment>());}

	/** The recursive algorithm. */
	protected Set<Fragment> visitRecursive(Tree tree, Set<Fragment> fragments)
	{
		while (/*!tree.isPreTerminal()&&*/tree.children().length == 1)
		{
			// skipping down
			tree = tree.getChild(0);
		}
		String phrase = phrase(tree);
		if (phrase.length() < PHRASE_MIN_LENGTH)
		{
			log.trace("phrase less than " + PHRASE_MIN_LENGTH + " characters, skipped: " + phrase);
			fragments.add(new Fragment(cube, phrase));
			return fragments;
		}

		if (phrase.length() > PHRASE_MAX_LENGTH)
		{
			log.trace("phrase '" + phrase + "' more than " + PHRASE_MAX_LENGTH + " characters, skipping matching try");
		} else
		{
			log.trace("visiting tree " + tree);
			log.trace("Phrase \"" + phrase + "\"...");
			// either we detected nothing or only part of the phrase

			Match matchResult = identify(phrase);
			// whole phrase matched, subtrees skipped
			if (!matchResult.isEmpty())
			{
				log.trace("matched to " + matchResult);
				fragments.add(matchResult.toFragment(cube));
				return fragments;
			}
		}
		// either we didn't match because the phrase is too long or matching didn't find anything, so match subtrees separately
		log.trace("unmatched, looking at subtrees");
		List<Fragment> childFragments = fragments(tree.getChildrenAsList(), x -> true);
		if (childFragments.isEmpty())
		{
			return new Fragment(cube, phrase);
		}
		List<Fragment> childFragmentsWithRefs = childFragments.stream().filter(f -> !f.isEmpty()).collect(Collectors.toList());
		List<Fragment> childFragmentsWithoutRefs = new LinkedList<>(childFragments);
		childFragmentsWithoutRefs.removeAll(childFragmentsWithRefs);

		List<Fragment> usefulChildFragments = new ArrayList<>(childFragmentsWithRefs);
		// we could throw unmatched fragments away but we try to combine them into something useful first
		if (!childFragmentsWithoutRefs.isEmpty())
		{
			String childFragmentsWithoutRefsPhrase = Fragment.combine(childFragmentsWithoutRefs).phrase;
			// too small, throw away
			if (childFragmentsWithoutRefsPhrase.length() < 3)
			{
				log.trace("unmatched fragment \"" + childFragmentsWithoutRefsPhrase + "\" length < 3, skipped");
			}
			// it's not small, but is it useful? do all the unmatched fragments match something?
			else
			{
				// TODO check partial combinations too
				Match unmatchedResult = identify(childFragmentsWithoutRefsPhrase);
				log.trace("unmatched fragments with phrase \"" + unmatchedResult.phrase + "\"");
				//				unmatchedFragments.stream().map(f->f.phrase).collect(Collectors.toList());

				if (unmatchedResult.isEmpty())
				{
					log.trace("unmatched fragment combination does not match anything.");
				} else
				{
					log.trace("unmatched fragment combination matched to " + unmatchedResult);
					usefulChildFragments.add(unmatchedResult.toFragment(cube));
				}
			}
		}
		if (usefulChildFragments.isEmpty())
		{
			log.trace("no match found for phrase \"" + phrase + "\"");
			return new Fragment(cube, phrase);
		} else
		{
			return Fragment.combine(usefulChildFragments);
		}
	}
}