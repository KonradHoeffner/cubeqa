package org.aksw.cubeqa.template;

import de.konradhoeffner.commons.ListTree;
import edu.stanford.nlp.trees.Tree;
import lombok.extern.slf4j.Slf4j;
import org.aksw.cubeqa.Cube;
import static org.aksw.cubeqa.StanfordTrees.phrase;

/** Generates the Cube Template. */
@Slf4j
public class WeightedTemplator extends Templator
{
	public WeightedTemplator(Cube cube) {super(cube);}

	public Template buildTemplate(String question)
	{
		preprocess(question);
		ListTree<Fragment> fragments = visitRecursive(root);

		//Template finalTemplate = Fragment.combine(Arrays.asList(rootFragment, detectFragment)).toTemplate(eats).get();
		//return finalTemplate;
		return null;
	}

	protected ListTree<Fragment> visitRecursive(Tree tree)
	{
		ListTree<Fragment> fragments = new ListTree<>(new Fragment(cube, phrase(tree)));
		visitRecursive(tree, fragments);
		return fragments;
	}

	/** The recursive algorithm. */
	protected void visitRecursive(Tree parseTree, ListTree<Fragment> parent)
	{
		String phrase = phrase(parseTree);
		if (phrase.length() < PHRASE_MIN_LENGTH)
		{
			log.trace("phrase less than " + PHRASE_MIN_LENGTH + " characters, skipped: " + phrase);
			parent.children.add(new ListTree<>(new Fragment(cube, phrase)));
			// don't go deeper, we are too short already
			return;
		}
		while (parseTree.children().length == 1)
		{
			parseTree = parseTree.getChild(0); // skipping down
		}

		// use empty fragment first, overwrite if we find a match
		ListTree<Fragment> cursor = new ListTree<>(new Fragment(cube, phrase));
		if (phrase.length() > PHRASE_MAX_LENGTH)
		{
			log.trace("phrase '" + phrase + "' more than " + PHRASE_MAX_LENGTH + " characters, skipping matching try");
			// as with the minimum length, we add an empty fragment, but this time we keep going down
		} else
		{
			log.trace("visiting tree " + parseTree);
			log.trace("Phrase \"" + phrase + "\"...");
			Match matchResult = identify(phrase);
			if (!matchResult.isEmpty())
			{
				log.trace("matched to " + matchResult);
				cursor = new ListTree<>(matchResult.toFragment(cube));
				// in the greedy algorithm we return here, because we found something, but for the weighted we keep looking for better options
				// did we match everything or just part?
				String leftover = phrase.replaceAll(matchResult.phrase, "").trim();
				if (!leftover.isEmpty())
				{
					// this leftover child will not be recursed but maybe it can be reused later in combination with something else
					parent.add(new Fragment(cube, leftover));
				}
			}
		}
		parent.children.add(cursor);

		// Match subtrees now, regardless of whether we matched something in this step or not.
		log.trace("unmatched, looking at subtrees");
		//List<Fragment> childFragments = parseTree.getChildrenAsList().stream().flatMap(t->visitRecursive(t).stream()).collect(Collectors.toList());
		for (Tree child : parseTree.getChildrenAsList())
		{
			visitRecursive(child, cursor);
		}
	}

//	void combine(ListTree<Fragment> fragments)
//	{
//		List<Fragment> childFragmentsWithRefs = fragments.stream().filter(f -> !f.isEmpty()).collect(Collectors.toList());
//		List<Fragment> childFragmentsWithoutRefs = new LinkedList<>(fragments);
//		childFragmentsWithoutRefs.removeAll(childFragmentsWithRefs);
//
//		List<Fragment> usefulChildFragments = new ArrayList<>(childFragmentsWithRefs);
//		// we could throw unmatched fragments away but we try to combine them into something useful first
//		if (!childFragmentsWithoutRefs.isEmpty())
//		{
//			String childFragmentsWithoutRefsPhrase = Fragment.combine(childFragmentsWithoutRefs).phrase;
//			// too small, throw away
//			if (childFragmentsWithoutRefsPhrase.length() < 3)
//			{
//				log.trace("unmatched fragment \"" + childFragmentsWithoutRefsPhrase + "\" length < 3, skipped");
//			}
//			// it's not small, but is it useful? do all the unmatched fragments match something?
//			else
//			{
//				// TODO check partial combinations too
//				Match unmatchedResult = identify(childFragmentsWithoutRefsPhrase);
//				log.trace("unmatched fragments with phrase \"" + unmatchedResult.phrase + "\"");
//				//				unmatchedFragments.stream().map(f->f.phrase).collect(Collectors.toList());
//
//				if (unmatchedResult.isEmpty())
//				{
//					log.trace("unmatched fragment combination does not match anything.");
//				} else
//				{
//					log.trace("unmatched fragment combination matched to " + unmatchedResult);
//					usefulChildFragments.add(unmatchedResult.toFragment(cube));
//				}
//			}
//		}
//		if (usefulChildFragments.isEmpty())
//		{
//			log.trace("no match found for phrase \"" + phrase + "\"");
//			parent.add(new Fragment(cube, phrase));
//			return parent;
//		} else
//		{
//			parent.add(Fragment.combine(usefulChildFragments));
//			return parent;
//		}
//	}

}