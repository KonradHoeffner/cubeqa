package org.aksw.cubeqa.detector;

import static org.junit.Assert.*;
import java.util.Set;
import org.aksw.cubeqa.Cube;
import org.aksw.cubeqa.restriction.Restriction;
import org.aksw.cubeqa.template.CubeTemplateFragment;
import org.junit.Test;

public class HalfInfiniteIntervalDetectorTest
{
	@Test public void testDetect()
	{
		Cube cube = Cube.finlandAid();
		Set<CubeTemplateFragment> fragments = HalfInfiniteIntervalDetector.INSTANCE.detect(cube,"How many countries had amounts of more than 1000000 € in 2010?");
		CubeTemplateFragment fragment = CubeTemplateFragment.combine(fragments);
		assertTrue(fragment.getRestrictions().size()==1);
		Restriction restriction = fragment.getRestrictions().iterator().next();
		assertEquals(restriction.getProperty().uri,"http://linkedspending.aksw.org/ontology/finland-aid-amount");
		assertTrue(restriction.wherePatterns().size()==2);
		assertTrue(restriction.wherePatterns().toString().contains("> \"1000000"));
	}
}