package org.aksw.autosparql.cube.property.scorer;

import org.aksw.autosparql.cube.Cube;
import org.aksw.autosparql.cube.property.scorer.old.DateScorer;
import org.junit.Test;

public class DateScorerTest
{

	@Test public void testScore()
	{
		Cube cube = Cube.getInstance("finland-aid");
		System.out.println(cube.properties);
		DateScorer scorer = new DateScorer(cube.properties.get("http://linkedspending.aksw.org/ontology/finland-aid-start-date"));
		String[] dates = {"2006-01-01","2006-01-02","2006-02-01","2014-04-01"};
		for(String date: dates)
		{
			System.out.println(date+": "+scorer.unsafeScore(date));
		}
	}

}