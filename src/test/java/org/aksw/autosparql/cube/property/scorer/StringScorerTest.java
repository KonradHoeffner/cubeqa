package org.aksw.autosparql.cube.property.scorer;

import org.aksw.autosparql.cube.Cube;
import org.junit.Test;

public class StringScorerTest
{

	@Test public void testScore()
	{
		Cube cube = Cube.getInstance("finland-aid");
		StringScorer scorer = new StringScorer(cube.properties.get("http://linkedspending.aksw.org/ontology/finland-aid-channel-of-delivery-name"));
		System.out.println(scorer.unsafeScore("Fida International"));
		System.out.println(scorer.unsafeScore("Fida"));
	}

}
