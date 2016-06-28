package org.aksw.cubeqa;

import edu.stanford.nlp.trees.Tree;
import java.util.List;
import java.util.Set;

/** Utility class for stanford trees.
 * @author Konrad Höffner */
public class StanfordTrees
{
	static public String phrase(Tree tree) {return tree.getLeaves().toString().replace(", ", " ").replaceAll("[\\[\\]]", "").trim();}

	// tree.remove is unsupported
	static public void removeChild(Tree tree, Tree child)
	{
		List<Tree> children = tree.getChildrenAsList();
		children.remove(child);
		tree.setChildren(children);
	}

	static public void removeChildren(Tree tree, Set<Tree> children)
	{
		List<Tree> allChildren = tree.getChildrenAsList();
		allChildren.removeAll(children);
		tree.setChildren(allChildren);
	}

	static public void removeSubtree(Tree tree, Tree child)
	{
		List<Tree> children = tree.getChildrenAsList();
		children.remove(child);
		tree.setChildren(children);
		for(Tree subTree: tree.getChildrenAsList()) removeSubtree(subTree, child);
	}

	static public boolean isTag(Tree tree, String tag) {return tree.label().value().equals(tag);}
}