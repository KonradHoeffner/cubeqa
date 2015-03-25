package org.aksw.cubeqa.benchmark;

import java.util.*;
import java.util.stream.Collectors;
import org.w3c.dom.*;

public class Nodes
{
	/** converts a NodeList to java.util.List of Node	 */
	static List<Node> list(NodeList nodeList)
	{
		List<Node> list = new ArrayList<>();
		for(int i=0;i<nodeList.getLength();i++) {list.add(nodeList.item(i));}
		return list;
	}

	static List<Element> childElements(Element e, String tagName)
	{
		return list(e.getElementsByTagName(tagName)).stream().map(node->(Element)node).collect(Collectors.toList());
	}

	static List<Element> childElements(Element e)
	{
		return list(e.getChildNodes()).stream().filter(c->c.getNodeType()==Node.ELEMENT_NODE).map(node->(Element)node).collect(Collectors.toList());
	}

	/** returns only the text directly in the tag but not in subtags.*/
	static String directTextContent(Node node)
	{
		return
		list(node.getChildNodes()).stream()
		.filter(c->c.getNodeType()==Node.TEXT_NODE)
		.map(Node::getTextContent)
		.reduce("",(s,t)->s+t);
	}
}