package org.aksw.cubeqa.template;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import edu.stanford.nlp.trees.Tree;

public class StanfordNlpTest
{
	@Test
	public void testParse()
	{
		Tree tree = StanfordNlp.parse("How much did the Philippines receive in the year of 2007?");
		assertEquals("(ROOT (SBARQ (WHADJP (WRB How) (RB much)) (SQ (VBD did) (NP (DT the) (NNP Philippines)) (VP (VB receive) (PP (IN in) (NP (NP (DT the) (NN year)) (PP (IN of) (NP (CD 2007))))))) (. ?)))", tree.toString());
	}
}