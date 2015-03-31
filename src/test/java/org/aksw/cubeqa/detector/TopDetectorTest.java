package org.aksw.cubeqa.detector;

import org.aksw.cubeqa.Cube;
import org.aksw.cubeqa.detector.TopDetector;
import org.junit.Test;

public class TopDetectorTest
{
	@Test public void testDetect()
	{
		Cube cube = Cube.FINLAND_AID;
		System.out.println(TopDetector.INSTANCE.detect(cube,"top 5 amounts"));
		System.out.println(TopDetector.INSTANCE.detect(cube,"7 lowest extended amounts"));
		System.out.println(TopDetector.INSTANCE.detect(cube,"Top 10 aid receivers"));
	}
}