package org.aksw.cubeqa.detector;

import static org.junit.Assert.*;
import org.aksw.cubeqa.Cube;
import org.junit.Test;

public class HalfInfiniteIntervalDetectorTest
{

	@Test public void testDetect()
	{
		Cube cube = Cube.FINLAND_AID;
		System.out.println(HalfInfiniteIntervalDetector.INSTANCE.detect(cube,"extended amounts of > 1000000 and amount < 100"));
	}

}
