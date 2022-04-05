package org.aksw.cubeqa.property.scorer;

import static org.junit.jupiter.api.Assertions.assertTrue;
import org.aksw.cubeqa.Cube;
import org.junit.jupiter.api.Test;

public class ObjectPropertyScorerTest
{
	@Test public void testRecipientCountry()
	{
		Cube cube = Cube.getInstance("finland-aid");
		Scorer scorer = cube.properties.get("http://linkedspending.aksw.org/ontology/finland-aid-recipient-country").scorer;
		assertTrue(scorer.getClass().equals(ObjectPropertyScorer.class));
		ScoreResult score = scorer.score("Tajikistan").get();

		assertTrue(score.value.equals("https://openspending.org/finland-aid/recipient-country/tj")&&score.score==1);
		score=scorer.score("Tajikystan").get();
		assertTrue(score.value.equals("https://openspending.org/finland-aid/recipient-country/tj")&&score.score<1&&score.score>0.6);
		score = scorer.score("Lao People's Democratic Republic").get();
		assertTrue(score.value.equals("https://openspending.org/finland-aid/recipient-country/la")&&score.score==1);
		score = scorer.score("Lao Peoples Demokratic Republic").get();
		assertTrue(score.value.equals("https://openspending.org/finland-aid/recipient-country/la")&&score.score<1&&score.score>0.6);
	}

	@Test public void testSector()
	{
		Cube cube = Cube.getInstance("finland-aid");

		Scorer scorer = cube.properties.get("http://linkedspending.aksw.org/ontology/finland-aid-sector").scorer;
		assertTrue(scorer.getClass().equals(ObjectPropertyScorer.class));
		{
		ScoreResult score = scorer.score("Strengthening civil society").get();
		assertTrue(score.value.equals("https://openspending.org/finland-aid/sector/15150")&&score.score==1);
		}
		{
		ScoreResult score = scorer.score("strengthen civil society").get();
		assertTrue(score.value.equals("https://openspending.org/finland-aid/sector/15150")&&score.score==1);
		}
		// not possible to match with levensthein automaton of with less than 3 max edit distance
//		score = scorer.score("Strengthen civil society").get();
//		assertTrue(score.value.equals("https://openspending.org/finland-aid/sector/15150")&&score.score<1&&score.score>0.6);
	}
}