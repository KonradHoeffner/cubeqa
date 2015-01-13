package org.aksw.autosparql.cube.template;

import static org.junit.Assert.*;
import org.aksw.autosparql.cube.Cube;
import org.junit.Test;

public class CubeTemplatorNewTest
{

	@Test public void test()
	{
		CubeTemplatorNew templator = new CubeTemplatorNew(Cube.getInstance("finland-aid"));
		MatchResult wholePhraseResult = templator.identify("How much money was invested to strengthen civil society in Yemen?");
		assertTrue(wholePhraseResult.toString(),wholePhraseResult.isEmpty());
		System.out.println(templator.identify("Yemen"));
		System.out.println(templator.identify("strengthen civil society"));

	}

}