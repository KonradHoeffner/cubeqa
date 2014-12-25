package org.aksw.autosparql.cube.property.scorer;

import static org.junit.Assert.assertTrue;
import org.aksw.autosparql.cube.Cube;
import org.junit.Test;

public class ObjectPropertyScorerTest
{

	@Test public void testScore()
	{
		Cube cube = Cube.getInstance("finland-aid");
		assertTrue(cube.properties.get("http://linkedspending.aksw.org/ontology/finland-aid-recipient-country").scorer.score("Tajikistan")>0.6);
	}
}