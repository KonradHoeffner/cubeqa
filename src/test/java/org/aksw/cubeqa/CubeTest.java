package org.aksw.cubeqa;

import static org.junit.Assert.*;
import java.io.File;
import org.aksw.cubeqa.Cube;
import org.junit.Test;

public class CubeTest
{

	@Test public void testSerialization()
	{
		Cube cube = Cube.getInstance("finland-aid");
		assertTrue(new File("cache/finland-aid.ser").exists());
		System.out.println(cube.properties.size());
		System.out.println(cube.properties);
	}

}
