package de.konradhoeffner.commons;

import java.util.ArrayList;
import java.util.List;

public class ListTree<T>
{
	public final T item;

	public ListTree (T item) {this.item=item;}

	public List<ListTree> children = new ArrayList<>();

	/** @return the items of all nodes in the tree	 */
	List<ListTree> items()
	{
		List<ListTree> items = new ArrayList<>(children);
		for (ListTree child: children) {items.addAll(child.items());}
		return items;
	}

	public void add(T item) {children.add(new ListTree<>(item));}

}