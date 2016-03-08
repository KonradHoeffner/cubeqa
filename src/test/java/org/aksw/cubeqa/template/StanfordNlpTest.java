package org.aksw.cubeqa.template;

import static org.junit.Assert.assertEquals;
import org.junit.Test;
import edu.stanford.nlp.trees.Tree;

public class StanfordNlpTest
{
	@Test
	public void testParse()
	{
		Tree tree = StanfordNlp.parse("How much did the Philippines receive in the year of 2007?");
		assertEquals("(ROOT (SBARQ (WHNP (WRB How) (RB much)) (SQ (VBD did) (NP (DT the) (NNPS Philippines)) (VP (VBP receive)"
				+ " (PP (IN in) (NP (NP (DT the) (NN year)) (PP (IN of) (NP (CD 2007))))))) (. ?)))", tree.toString());
	}
}