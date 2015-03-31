package org.aksw.cubeqa.detector;

import static org.junit.Assert.*;
import org.aksw.cubeqa.Cube;
import org.junit.Test;

public class PerTimeDetectorTest
{

	@Test public void testDetect()
	{
		 System.out.println(PerTimeDetector.INSTANCE.detect(Cube.FINLAND_AID, "per year"));
	}

}
