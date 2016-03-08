package org.aksw.cubeqa.template;

import static org.junit.Assert.assertTrue;
import org.aksw.cubeqa.Cube;
import org.junit.Test;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CubeTemplatorNewTest
{
	@Test public void templatorTest()
	{
		Templator templator = new Templator(Cube.getInstance("finland-aid"));
		Match wholePhraseResult = templator.identify("How much money was invested to strengthen civil society in Yemen?");
		assertTrue(wholePhraseResult.toString(),wholePhraseResult.isEmpty());
		log.debug("{}",templator.identify("Yemen"));
		log.debug("{}",templator.identify("strengthening civil society"));
	}
}