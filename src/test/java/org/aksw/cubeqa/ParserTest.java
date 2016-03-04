package org.aksw.cubeqa;

import static org.junit.Assert.*;
import org.aksw.cubeqa.template.StanfordNlp;
import org.junit.Test;
import edu.stanford.nlp.trees.Tree;

public class ParserTest
{

	@Test public void test()
	{
		Tree parsed = StanfordNlp.parse("How much did the top 10 aided countries get in 2008?");
		System.out.println(parsed);
	}

}
