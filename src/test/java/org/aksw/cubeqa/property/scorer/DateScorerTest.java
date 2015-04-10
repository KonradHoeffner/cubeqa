package org.aksw.cubeqa.property.scorer;

import static org.junit.Assert.*;
import org.aksw.cubeqa.Cube;
import org.aksw.cubeqa.property.scorer.temporal.TemporalScorer;
import org.junit.Test;

public class DateScorerTest
{
	@Test public void testScore()
	{
		Cube cube = Cube.getInstance("finland-aid");
		Scorer scorer = TemporalScorer.dateScorer(cube.properties.get("http://linkedspending.aksw.org/ontology/finland-aid-start-date"));
//		String[] dates = {"2006-01-01","2006-01-02","2006-02-01","2014-04-01"};
		assertEquals(scorer.score("2006-01-01").get().property.uri,"http://linkedspending.aksw.org/ontology/finland-aid-start-date");
		assertEquals(scorer.score("2006-02-01").get().property.uri,"http://linkedspending.aksw.org/ontology/finland-aid-start-date");
		assertFalse(scorer.score("2006-01-02").isPresent());
		assertFalse(scorer.score("2014-04-01").isPresent());
	}
}