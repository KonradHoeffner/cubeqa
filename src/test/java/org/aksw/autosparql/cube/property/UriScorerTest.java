package org.aksw.autosparql.cube.property;

import static org.junit.Assert.*;
import java.util.Arrays;
import org.aksw.autosparql.cube.Cube;
import org.junit.Test;

public class UriScorerTest
{

	@Test public void testScore()
	{
		Cube cube = Cube.getInstance("finland-aid");
		UriScorer scorer = new UriScorer(cube,cube.properties.get("http://linkedspending.aksw.org/ontology/finland-aid-recipient-country"));
		assertTrue(scorer.score("https://openspending.org/finland-aid/recipient-country/et")>0.6);
	}

}
