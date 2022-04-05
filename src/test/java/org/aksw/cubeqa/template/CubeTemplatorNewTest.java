package org.aksw.cubeqa.template;

import static org.junit.jupiter.api.Assertions.assertTrue;
import org.aksw.cubeqa.Cube;
import org.junit.jupiter.api.Test;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CubeTemplatorNewTest
{
	@Test public void templatorTest()
	{
		Templator templator = new GreedyTemplator(Cube.getInstance("finland-aid"));
		Match wholePhraseResult = templator.identify("How much money was invested to strengthen civil society in Yemen?");
		assertTrue(wholePhraseResult.isEmpty(),wholePhraseResult.toString());
		log.debug("{}",templator.identify("Yemen"));
		log.debug("{}",templator.identify("strengthening civil society"));
	}
}