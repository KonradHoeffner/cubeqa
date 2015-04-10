package org.aksw.cubeqa.index;

import static org.junit.Assert.*;
import org.aksw.cubeqa.Cube;
import org.aksw.cubeqa.property.ComponentProperty;
import org.junit.Test;
import com.google.common.collect.Range;

public class LabelIndexTest
{

	@Test public void test()
	{
		Cube cube = Cube.getInstance("finland-aid");
		ComponentProperty property = cube.properties.get("http://linkedspending.aksw.org/ontology/finland-aid-recipient-country");

		assertTrue(Range.<Double>closed(0.5, 0.95).contains(property.scorer.score("Egyppt").get().score));
		assertEquals(1,property.scorer.score("Egypt").get().score,0);
	}

}