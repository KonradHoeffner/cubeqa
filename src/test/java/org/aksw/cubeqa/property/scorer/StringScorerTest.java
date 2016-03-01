package org.aksw.cubeqa.property.scorer;

import static org.junit.Assert.assertEquals;
import org.aksw.cubeqa.Config;
import org.aksw.cubeqa.Cube;
import org.junit.Test;

public class StringScorerTest
{
	@Test public void testChannelOfDeliveryName()
	{
		Cube cube = Cube.getInstance("finland-aid");
		Scorer scorer = cube.properties.get("http://linkedspending.aksw.org/ontology/finland-aid-channel-of-delivery-name").scorer;
		assertEquals(StringScorer.class,scorer.getClass());
		ScoreResult score = scorer.score("Finnish Red Cross").get();
		assertEquals("Finnish Red Cross",score.value);
		assertEquals(Config.INSTANCE.boostString,score.score,0);
	}
}