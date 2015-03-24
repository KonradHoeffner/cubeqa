package org.aksw.cubeqa.benchmark;

import static org.junit.Assert.assertTrue;
import java.util.Arrays;
import java.util.HashSet;
import org.junit.Test;

public class PerformanceTest
{

	@Test public void test()
	{
		Performance p1 = Performance.performance(new HashSet<>(Arrays.asList("Alice","Trudy","Bob","John")), new HashSet<>(Arrays.asList("Alice","Marvin")));
		assertTrue(p1.equals(new Performance(0.5, 0.25)));
	}

}