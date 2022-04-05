package org.aksw.cubeqa.detector;

import static org.junit.jupiter.api.Assertions.*;
import java.util.Set;
import org.aksw.cubeqa.Cube;
import org.aksw.cubeqa.restriction.Restriction;
import org.aksw.cubeqa.template.Fragment;
import org.junit.jupiter.api.Test;

public class HalfInfiniteIntervalDetectorTest
{
	@Test public void testDetect()
	{
		Cube cube = Cube.finlandAid();
		Set<Fragment> fragments = HalfInfiniteIntervalDetector.INSTANCE.detect(cube,"How many countries had amounts of more than 1000000 € in 2010?");
		Fragment fragment = Fragment.combine(fragments);
		assertTrue(fragment.getRestrictions().size()==1);
		Restriction restriction = fragment.getRestrictions().iterator().next();
		assertEquals(restriction.getProperty().uri,"http://linkedspending.aksw.org/ontology/finland-aid-amount");
		assertTrue(restriction.wherePatterns().size()==2);
		assertTrue(restriction.wherePatterns().toString().contains("> \"1000000"));
	}
}