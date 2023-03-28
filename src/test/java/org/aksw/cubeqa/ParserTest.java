package org.aksw.cubeqa;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.aksw.cubeqa.template.StanfordNlp;
import org.junit.jupiter.api.Test;
import edu.stanford.nlp.trees.Tree;

public class ParserTest
{
	@Test
	public void test()
	{
		Tree parsed = StanfordNlp.parse("How much did the top 10 aided countries get in 2008?");
		assertEquals(
				"(ROOT (SBARQ (WHADJP (WRB How) (RB much)) (SQ (VBD did) (NP (DT the) (JJ top) (CD 10)) (VP (VBN aided) (SBAR (S (NP (NNS countries)) (VP (VBP get) (PP (IN in) (NP (CD 2008)))))))) (. ?)))",
				parsed.toString());
	}

}