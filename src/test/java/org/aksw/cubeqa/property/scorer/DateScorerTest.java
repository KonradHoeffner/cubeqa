package org.aksw.cubeqa.property.scorer;

import static org.junit.jupiter.api.Assertions.*;
import org.aksw.cubeqa.Cube;
import org.aksw.cubeqa.property.scorer.temporal.TemporalScorer;
import org.junit.jupiter.api.Test;

public class DateScorerTest
{
	private static final String START_DATE = "http://linkedspending.aksw.org/ontology/finland-aid-start-date";

	@Test public void testScore()
	{
		Cube cube = Cube.getInstance("finland-aid");
		TemporalScorer scorer = TemporalScorer.dateScorer(cube.properties.get(START_DATE));
//		String[] dates = {"2006-01-01","2006-01-02","2006-02-01","2014-04-01"};
		assertEquals(scorer.score("2007-03-01").get().property.uri,START_DATE);
		assertEquals(scorer.score("2006-01-31").get().property.uri,START_DATE);
		assertEquals(scorer.score("2006-02-01").get().property.uri,START_DATE);
		// the next day of a day that is in
		assertFalse(scorer.score("2006-03-02").isPresent());		
		// not in at all
		assertFalse(scorer.score("2006-01-02").isPresent());
		assertFalse(scorer.score("2014-04-01").isPresent());
	}
}