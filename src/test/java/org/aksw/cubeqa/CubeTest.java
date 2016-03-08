package org.aksw.cubeqa;

import static org.junit.Assert.*;
import java.io.File;
import org.aksw.cubeqa.Cube;
import org.junit.Test;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CubeTest
{

	@Test public void testSerialization()
	{
		Cube cube = Cube.getInstance("finland-aid");
		assertTrue(new File("cache/finland-aid.ser").exists());
		log.debug("{}",cube.properties.size());
		log.debug("{}",cube.properties);
	}

}