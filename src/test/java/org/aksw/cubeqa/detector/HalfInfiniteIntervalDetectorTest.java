package org.aksw.cubeqa.detector;

import java.util.Set;
import org.aksw.cubeqa.Cube;
import org.aksw.cubeqa.template.CubeTemplateFragment;
import org.junit.Test;

public class HalfInfiniteIntervalDetectorTest
{

	@Test public void testDetect()
	{
		Cube cube = Cube.FINLAND_AID;
		Set<CubeTemplateFragment> fragments = HalfInfiniteIntervalDetector.INSTANCE.detect(cube,"How many countries had amounts of more than 1000000 € in 2010?");
		CubeTemplateFragment fragment = CubeTemplateFragment.combine(fragments);
//		assertTrue(fragment.getRestrictions())
//		System.out.println(HalfInfiniteIntervalDetector.INSTANCE.detect(cube,"extended amounts of > 1000000 and amount < 100"));
	}

}