package org.aksw.cubeqa.template;

import static org.junit.Assert.assertTrue;
import org.aksw.cubeqa.Cube;
import org.junit.Test;

public class CubeTemplatorNewTest
{
	@Test public void templatorTest()
	{
		CubeTemplator templator = new CubeTemplator(Cube.getInstance("finland-aid"));
		MatchResult wholePhraseResult = templator.identify("How much money was invested to strengthen civil society in Yemen?");
		assertTrue(wholePhraseResult.toString(),wholePhraseResult.isEmpty());
		System.out.println(templator.identify("Yemen"));
		System.out.println(templator.identify("strengthening civil society"));
	}
}