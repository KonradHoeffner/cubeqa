package org.aksw.cubeqa.template;

import de.konradhoeffner.commons.ListTree;
import de.konradhoeffner.commons.StopWatch;
import edu.stanford.nlp.trees.Tree;
import lombok.extern.slf4j.Slf4j;
import org.aksw.cubeqa.Cube;
import static org.aksw.cubeqa.StanfordTrees.phrase;
import org.aksw.cubeqa.StopWatches;
import org.aksw.cubeqa.detector.Aggregate;
import org.aksw.cubeqa.property.ComponentProperty;
import org.aksw.cubeqa.restriction.Restriction;
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
		ListTree<Fragment> fragments = visitRecursive(root);

		//Template finalTemplate = Fragment.combine(Arrays.asList(rootFragment, detectFragment)).toTemplate(eats).get();
		//return finalTemplate;
		return Fragment.combine(Arrays.asList(detectFragment,Fragment.combine(fragments.items()))).toTemplate(eats).get();
	}

	protected ListTree<Fragment> visitRecursive(Tree tree)
	{
		ListTree<Fragment> fragments = new ListTree<>(new Fragment(cube, phrase(tree)));
		visitRecursive(tree, fragments, 0);
		return fragments;
	}

	/** The recursive algorithm. */
	protected void visitRecursive(Tree parseTree, ListTree<Fragment> parent, int depth)
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

		ListTree<Fragment> cursor = null;
		if(depth==0) {cursor=parent;} // don't match the full question (could be changed later for higher recall, lower precision?)
		else
		{
			if (phrase.length() > PHRASE_MAX_LENGTH)
			{
				log.trace("phrase '" + phrase + "' more than " + PHRASE_MAX_LENGTH + " characters, skipping matching try");
				// as with the minimum length, we add an empty fragment, but this time we keep going down
				cursor = new ListTree<>(new Fragment(cube, phrase));
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
					// don't use leftovers right now because we keep recursing anyways
					// did we match everything or just part?
					// String leftover = phrase.replaceAll(matchResult.phrase, "").trim();
					//	if (!leftover.isEmpty())
					//	{
					//	// this leftover child will not be recursed but maybe it can be reused later in combination with something else
					//	parent.add(new Fragment(cube, leftover));
					//	}
				}
				else
				{
					cursor = new ListTree<>(new Fragment(cube, phrase));
				}
			}
			parent.children.add(cursor);
		}

		// Match subtrees now, regardless of whether we matched something in this step or not.
		log.trace("looking at subtrees");
		//List<Fragment> childFragments = parseTree.getChildrenAsList().stream().flatMap(t->visitRecursive(t).stream()).collect(Collectors.toList());
		for (Tree child : parseTree.getChildrenAsList())
		{
			visitRecursive(child, cursor,depth+1);
		}
	}

//	Fragment combine(ListTree<Fragment> fragments)
//	{
//		List<Fragment> childFragmentsWithRefs = fragments.items().stream().filter(f -> !f.isEmpty()).collect(Collectors.toList());
//		List<Fragment> childFragmentsWithoutRefs = new LinkedList<>(fragments.items());
//		childFragmentsWithoutRefs.removeAll(childFragmentsWithRefs);
//
//		List<Fragment> usefulChildFragments = new ArrayList<>(childFragmentsWithRefs);
//		// we could throw unmatched fragments away but we try to combine them into something useful first
////		if (!childFragmentsWithoutRefs.isEmpty())
////		{
////			String childFragmentsWithoutRefsPhrase = Fragment.combine(childFragmentsWithoutRefs).phrase;
////			// too small, throw away
////			if (childFragmentsWithoutRefsPhrase.length() < 3)
////			{
////				log.trace("unmatched fragment \"" + childFragmentsWithoutRefsPhrase + "\" length < 3, skipped");
////			}
////			// it's not small, but is it useful? do all the unmatched fragments match something?
////			else
////			{
////				// TODO check partial combinations too
////				Match unmatchedResult = identify(childFragmentsWithoutRefsPhrase);
////				log.trace("unmatched fragments with phrase \"" + unmatchedResult.phrase + "\"");
////				//				unmatchedFragments.stream().map(f->f.phrase).collect(Collectors.toList());
////
////				if (unmatchedResult.isEmpty())
////				{
////					log.trace("unmatched fragment combination does not match anything.");
////				} else
////				{
////					log.trace("unmatched fragment combination matched to " + unmatchedResult);
////					usefulChildFragments.add(unmatchedResult.toFragment(cube));
////				}
////			}
////		}
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
	
/*
	public static Fragment combine(ListTree<Fragment> fragments)
	{
		StopWatch fragmentCombineWatch = StopWatches.INSTANCE.getWatch("fragmentcombine");
		fragmentCombineWatch.start();
//		if(fragments.isEmpty())
		//{throw new IllegalArgumentException("empty fragment set, can't combine");}
		//		{log.warn("empty fragment set, combination empty");}

		// *** new sets are unions over all fragment sets **********************************************************
		if(fragments.nodes().stream().map(f->f.item.cube.uri).collect(Collectors.toSet()).size()>1) {
			throw new IllegalArgumentException("different cube uris, can't combine");
		}
		// TODO join restrictions if possible (e.g. intervals for numericals, detect impossibilities)
		Set<Restriction> restrictions = new HashSet<>();
		Set<ComponentProperty> answerProperties = new HashSet<>();
		Set<ComponentProperty> perProperties = new HashSet<>();
		Set<Aggregate> aggregates = new HashSet<>();
		Set<Match> matchResults = new HashSet<>();
		fragments.nodes().forEach(node->
		{
			Fragment f = node.item;
			restrictions.addAll(f.restrictions);
			answerProperties.addAll(f.answerProperties);
			perProperties.addAll(f.perProperties);
			aggregates.addAll(f.aggregates);
		});
		// *** phrases are added in list order with space in between ***********************************************
		String combinedPhrase = fragments.stream().map(Fragment::getPhrase).reduce("", (a,b)->a+" "+b).trim();
		Fragment fragment = new Fragment(fragments.iterator().next().cube,combinedPhrase,
				restrictions, answerProperties, perProperties, aggregates,matchResults);

		// *** combining match results *****************************************************************************
		// **** get all properties that are not yet assigned but somewhere referenced both as name and as value
		// strictly, they should be referenced in different matchresult objects but that calculation would be too complicated, sort that out later
		Set<ComponentProperty> properties = fragment.unreferredProperties();
		Set<Match> fragmentsMatchResults = fragments.stream().map(Fragment::getMatches).map(Set::stream).flatMap(id->id).collect(Collectors.toSet());
		properties.retainAll(fragmentsMatchResults.stream().map(mr->mr.nameRefs.keySet()).flatMap(Set::stream).collect(Collectors.toSet()));
		properties.retainAll(fragmentsMatchResults .stream().map(mr->mr.valueRefs.keySet()).flatMap(Set::stream).collect(Collectors.toSet()));
		for(ComponentProperty property: properties)
		{
			// greedy algorithm, does not work when highestNameRef has the only value Ref TODO intelligently check more pairs
			// we should always get a highest name in the first iteration per construction of fragmentsMatchResults
			// but later this one can be used for another property, so use ifpresent

			fragmentsMatchResults.stream().max(Comparator.comparingDouble(mr->mr.nameRefs.get(property)==null?0:mr.nameRefs.get(property)))
					.ifPresent(highestNameRef->
					{
						fragmentsMatchResults.stream().filter(mr->mr!=highestNameRef)
								.max(Comparator.comparingDouble(mr->mr.valueRefs.get(property)==null?0:mr.valueRefs.get(property).score))
								.ifPresent(highestValueRef->
								{
									if(highestNameRef.nameRefs.get(property)!=null&&highestValueRef.valueRefs.get(property)!=null)
									{
										double score = highestNameRef.nameRefs.get(property)*highestValueRef.valueRefs.get(property).score;
										if(score>MIN_COMBINED_SCORE)
										{
											restrictions.add(highestValueRef.valueRefs.get(property).toRestriction());
											fragmentsMatchResults.remove(highestNameRef);
											fragmentsMatchResults.remove(highestValueRef);
										}
									}
								});
					});
		}
		// add back all non used match results
		matchResults.addAll(fragmentsMatchResults);
		// **** end combining match resuls *************************************************************************

		//		Set<ComponentProperty> nameValue = this.nameRefs.keySet();
		//		nameValue.retainAll(otherResult.valueRefs.keySet());

		fragmentCombineWatch.stop();
		return fragment;
	}
	*/

}