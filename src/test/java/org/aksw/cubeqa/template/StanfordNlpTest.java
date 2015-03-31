package org.aksw.cubeqa.template;

import org.junit.Test;

public class StanfordNlpTest
{
	@Test public void testParse()
	{
		System.out.println(StanfordNlp.parse("How much money was given to strengthen civil society in Yemen?"));
	}
}