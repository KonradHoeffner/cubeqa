package org.aksw.autosparql.cube.detector;

import static org.junit.Assert.*;
import org.junit.Test;

public class IntervalDetectorTest
{

	@Test public void testDetect()
	{
		System.out.println(TopDetector.INSTANCE.detect("10 poorest countries"));
		System.out.println(TopDetector.INSTANCE.detect("top 5 beaches"));
		System.out.println(TopDetector.INSTANCE.detect("7 lowest prices"));
		System.out.println(TopDetector.INSTANCE.detect("Top 10 aid receivers"));
	}

}
