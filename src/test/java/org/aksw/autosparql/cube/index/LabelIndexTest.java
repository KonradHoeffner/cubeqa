package org.aksw.autosparql.cube.index;

import static org.junit.Assert.*;
import org.aksw.autosparql.cube.Cube;
import org.aksw.autosparql.cube.property.ComponentProperty;
import org.junit.Test;
import com.google.common.collect.Range;

public class LabelIndexTest
{

	@Test public void test()
	{
		Cube cube = Cube.getInstance("finland-aid");
		ComponentProperty property = cube.properties.get("http://linkedspending.aksw.org/ontology/finland-aid-recipient-country");
		// TODO score is too high, calculate final score on my own?

		assertTrue(Range.<Double>closed(0.5, 0.95).contains(property.scorer.score("Egyppt").get().score));
		assertEquals(1,property.scorer.score("Egypt").get().score,0);
	}

}