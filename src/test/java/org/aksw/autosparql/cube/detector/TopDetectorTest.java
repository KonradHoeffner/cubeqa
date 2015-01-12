package org.aksw.autosparql.cube.detector;

import org.junit.Test;

public class TopDetectorTest
{

	@Test public void testDetect()
	{
		System.out.println(TopDetector.INSTANCE.detect("5 highest mountains"));
		System.out.println(TopDetector.INSTANCE.detect("top 5 beaches"));
		System.out.println(TopDetector.INSTANCE.detect("7 lowest prices"));
	}

}