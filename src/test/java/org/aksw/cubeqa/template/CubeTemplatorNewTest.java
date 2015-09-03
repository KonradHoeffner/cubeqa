package org.aksw.cubeqa.template;

import static org.junit.Assert.*;
import org.aksw.cubeqa.Cube;
import org.aksw.cubeqa.template.CubeTemplator;
import org.aksw.cubeqa.template.MatchResult;
import org.junit.Test;

public class CubeTemplatorNewTest
{

	@Test public void test()
	{
		CubeTemplator templator = new CubeTemplator(Cube.getInstance("finland-aid"));
		MatchResult wholePhraseResult = templator.identify("How much money was invested to strengthen civil society in Yemen?",0);
		assertTrue(wholePhraseResult.toString(),wholePhraseResult.isEmpty());
		System.out.println(templator.identify("Yemen",0));
		System.out.println(templator.identify("strengthening civil society",0));

	}

}