package org.aksw.cubeqa.detector;

import static org.junit.Assert.*;
import org.aksw.cubeqa.Cube;
import org.aksw.cubeqa.property.ComponentProperty;
import org.junit.Test;

public class PerTimeDetectorTest
{

	@Test public void testDetect()
	{
		ComponentProperty refYear = Cube.finlandAid().properties.get("http://linkedspending.aksw.org/ontology/refYear");
		ComponentProperty refMonth= Cube.finlandAid().properties.get("http://linkedspending.aksw.org/ontology/refMonth");
		ComponentProperty refDay  = Cube.finlandAid().properties.get("http://linkedspending.aksw.org/ontology/refDay");

		assertTrue(PerTimeDetector.INSTANCE.detect(Cube.finlandAid(), "per year").iterator().next().getPerProperties().contains(refYear));
		assertTrue(PerTimeDetector.INSTANCE.detect(Cube.finlandAid(), " per year").iterator().next().getPerProperties().contains(refYear));
		assertTrue(PerTimeDetector.INSTANCE.detect(Cube.finlandAid(), " yearly.").iterator().next().getPerProperties().contains(refYear));

		assertTrue(PerTimeDetector.INSTANCE.detect(Cube.finlandAid(), "monthly").iterator().next().getPerProperties().contains(refMonth));
		assertTrue(PerTimeDetector.INSTANCE.detect(Cube.finlandAid(), "daily").iterator().next().getPerProperties().contains(refDay));

		assertTrue(PerTimeDetector.INSTANCE.detect(Cube.finlandAid(), "hyper year").isEmpty());
		assertTrue(PerTimeDetector.INSTANCE.detect(Cube.finlandAid(), "per yearo").isEmpty());

	//	assertTrue(PerTimeDetector.INSTANCE.detect(Cube.FINLAND_AID(), "per year per month").size()==2); // doesn't make sense
	}

}