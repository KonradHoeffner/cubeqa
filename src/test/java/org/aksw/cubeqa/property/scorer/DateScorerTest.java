package org.aksw.cubeqa.property.scorer;

import org.aksw.cubeqa.Cube;
import org.aksw.cubeqa.property.scorer.temporal.TemporalScorer;
import org.junit.Test;

public class DateScorerTest
{

	@Test public void testScore()
	{
		// TODO is this correct?
		Cube cube = Cube.getInstance("finland-aid");
		System.out.println(cube.properties);
		Scorer scorer = TemporalScorer.dateScorer(cube.properties.get("http://linkedspending.aksw.org/ontology/finland-aid-start-date"));
		String[] dates = {"2006-01-01","2006-01-02","2006-02-01","2014-04-01"};
		for(String date: dates)
		{
			System.out.println(date+": "+scorer.unsafeScore(date));
		}
	}

}