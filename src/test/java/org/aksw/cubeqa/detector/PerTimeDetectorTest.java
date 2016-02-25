package org.aksw.cubeqa.detector;

import static org.junit.Assert.assertTrue;
import org.aksw.cubeqa.Cube;
import org.aksw.cubeqa.property.ComponentProperty;
import org.junit.Test;

public class PerTimeDetectorTest
{

	@Test public void testDetect()
	{
		ComponentProperty refYear = Cube.finlandAid().properties.get("http://linkedspending.aksw.org/ontology/refYear");
		//		ComponentProperty refMonth= Cube.finlandAid().properties.get("http://linkedspending.aksw.org/ontology/refMonth");
		//		ComponentProperty refDay  = Cube.finlandAid().properties.get("http://linkedspending.aksw.org/ontology/refDay");		
		ComponentProperty refDate = Cube.finlandAid().properties.get("http://linkedspending.aksw.org/ontology/refDate");

		assertTrue(PerTimeDetector.INSTANCE.detect(Cube.finlandAid(), "per year").iterator().next().getPerProperties().contains(refYear));
		assertTrue(PerTimeDetector.INSTANCE.detect(Cube.finlandAid(), " per year").iterator().next().getPerProperties().contains(refYear));
		assertTrue(PerTimeDetector.INSTANCE.detect(Cube.finlandAid(), " yearly.").iterator().next().getPerProperties().contains(refYear));

		// refMonth is not used in the QALD 6 Task 3 Dataset
		//		assertTrue(PerTimeDetector.INSTANCE.detect(Cube.finlandAid(), "monthly").iterator().next().getPerProperties().contains(refMonth));
		// refDay is not used in the QALD 6 Task 3 Dataset
		//		assertTrue(PerTimeDetector.INSTANCE.detect(Cube.finlandAid(), "daily").iterator().next().getPerProperties().contains(refDay));
		assertTrue(PerTimeDetector.INSTANCE.detect(Cube.finlandAid(), "daily").iterator().next().getPerProperties().contains(refDate));

		assertTrue(PerTimeDetector.INSTANCE.detect(Cube.finlandAid(), "hyper year").isEmpty());
		assertTrue(PerTimeDetector.INSTANCE.detect(Cube.finlandAid(), "per yearo").isEmpty());
	}

}