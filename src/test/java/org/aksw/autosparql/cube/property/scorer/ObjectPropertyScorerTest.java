package org.aksw.autosparql.cube.property.scorer;

import static org.junit.Assert.*;
import java.util.Arrays;
import org.aksw.autosparql.cube.Cube;
import org.aksw.autosparql.cube.property.scorer.ObjectPropertyScorer;
import org.junit.Test;

public class ObjectPropertyScorerTest
{

	@Test public void testScore()
	{
		Cube cube = Cube.getInstance("finland-aid");
		ObjectPropertyScorer scorer = new ObjectPropertyScorer(cube.properties.get("http://linkedspending.aksw.org/ontology/finland-aid-recipient-country"));
		assertTrue(scorer.unsafeScore("https://openspending.org/finland-aid/recipient-country/et")>0.6);
	}

}