package de.konradhoeffner.commons;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ListTree<T>
{
	public final T item;

	public ListTree (T item) {this.item=item;}

	public List<ListTree<T>> children = new ArrayList<>();

	/** @return all nodes in the tree	 */
	public List<ListTree<T>> nodes()
	{
		List<ListTree<T>> nodes = new ArrayList<>(children);
		for (ListTree<T> child: children) {nodes.addAll(child.nodes());}
		return nodes;
	}

	/** @return the items of all nodes in the tree	 */
	public List<T> items()
	{
		return nodes().stream().map(n->n.item).collect(Collectors.toList());
	}

	public void add(T item) {children.add(new ListTree<>(item));}

}