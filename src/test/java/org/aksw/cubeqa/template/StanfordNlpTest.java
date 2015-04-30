package org.aksw.cubeqa.template;

import org.junit.Test;

public class StanfordNlpTest
{
	@Test public void testParse()
	{
		System.out.println(StanfordNlp.parse("How much did the Philippines receive in the year of 2007?"));
	}
}