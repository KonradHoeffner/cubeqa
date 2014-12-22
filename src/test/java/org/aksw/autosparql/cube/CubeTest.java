package org.aksw.autosparql.cube;

import static org.junit.Assert.*;
import java.io.File;
import org.junit.Test;

public class CubeTest
{

	@Test public void testSerialization()
	{
		Cube cube = Cube.getInstance("finland-aid");
		assertTrue(new File("cache/finland-aid.ser").exists());

	}

}
