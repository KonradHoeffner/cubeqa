package org.aksw.cubeqa;

import static org.junit.jupiter.api.Assertions.assertTrue;
import java.io.File;
import org.junit.jupiter.api.Test;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CubeTest
{

	@Test public void testSerialization()
	{
		Cube cube = Cube.getInstance("finland-aid");
		assertTrue(new File(Files.localFolder("cache"),"finland-aid.ser").exists());
		log.debug("{}",cube.properties.size());
		log.debug("{}",cube.properties);
	}

}