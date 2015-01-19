package org.aksw.autosparql.cube.detector;

import org.aksw.autosparql.cube.Cube;
import org.junit.Test;

public class TopDetectorTest
{
	@Test public void testDetect()
	{
		Cube cube = Cube.FINLAND_AID;
		System.out.println(TopDetector.INSTANCE.detect(cube,"10 poorest countries"));
		System.out.println(TopDetector.INSTANCE.detect(cube,"top 5 beaches"));
		System.out.println(TopDetector.INSTANCE.detect(cube,"7 lowest prices"));
		System.out.println(TopDetector.INSTANCE.detect(cube,"Top 10 aid receivers"));
	}
}