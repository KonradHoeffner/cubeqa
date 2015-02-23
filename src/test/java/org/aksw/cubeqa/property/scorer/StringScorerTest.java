package org.aksw.cubeqa.property.scorer;

import org.aksw.cubeqa.Cube;
import org.aksw.cubeqa.property.scorer.StringScorer;
import org.junit.Test;

public class StringScorerTest
{

	@Test public void testScore()
	{
		Cube cube = Cube.getInstance("finland-aid");
		StringScorer scorer = new StringScorer(cube.properties.get("http://linkedspending.aksw.org/ontology/finland-aid-channel-of-delivery-name"));
		System.out.println(scorer.unsafeScore("Finnfund"));
		System.out.println(scorer.unsafeScore("Fida International"));
		System.out.println(scorer.unsafeScore("Fida"));
	}

}
