package org.aksw.cubeqa.detector;

import static org.junit.Assert.*;
import org.aksw.cubeqa.Cube;
import org.aksw.cubeqa.property.ComponentProperty;
import org.junit.Test;

public class PerTimeDetectorTest
{

	@Test public void testDetect()
	{
		ComponentProperty refYear = Cube.FINLAND_AID.properties.get("http://linkedspending.aksw.org/ontology/refYear");
		ComponentProperty refMonth= Cube.FINLAND_AID.properties.get("http://linkedspending.aksw.org/ontology/refMonth");
		ComponentProperty refDay  = Cube.FINLAND_AID.properties.get("http://linkedspending.aksw.org/ontology/refDay");

		assertTrue(PerTimeDetector.INSTANCE.detect(Cube.FINLAND_AID, "per year").iterator().next().getPerProperties().contains(refYear));
		assertTrue(PerTimeDetector.INSTANCE.detect(Cube.FINLAND_AID, " per year").iterator().next().getPerProperties().contains(refYear));
		assertTrue(PerTimeDetector.INSTANCE.detect(Cube.FINLAND_AID, " yearly.").iterator().next().getPerProperties().contains(refYear));

		assertTrue(PerTimeDetector.INSTANCE.detect(Cube.FINLAND_AID, "monthly").iterator().next().getPerProperties().contains(refMonth));
		assertTrue(PerTimeDetector.INSTANCE.detect(Cube.FINLAND_AID, "daily").iterator().next().getPerProperties().contains(refDay));

		assertTrue(PerTimeDetector.INSTANCE.detect(Cube.FINLAND_AID, "hyper year").isEmpty());
		assertTrue(PerTimeDetector.INSTANCE.detect(Cube.FINLAND_AID, "per yearo").isEmpty());

	//	assertTrue(PerTimeDetector.INSTANCE.detect(Cube.FINLAND_AID, "per year per month").size()==2); // doesn't make sense
	}

}