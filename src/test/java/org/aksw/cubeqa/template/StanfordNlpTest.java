package org.aksw.cubeqa.template;

import org.junit.Test;
import edu.stanford.nlp.trees.Tree;

public class StanfordNlpTest
{
	@Test public void testParse()
	{
		Tree tree = StanfordNlp.parse("How much did the Philippines receive in the year of 2007?");
		System.out.println(tree);
	}
}