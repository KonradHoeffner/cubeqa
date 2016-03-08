package org.aksw.cubeqa.property.scorer;

import static org.junit.Assert.assertEquals;
import org.aksw.cubeqa.Config;
import org.aksw.cubeqa.Cube;
import org.junit.Test;

public class StringScorerTest
{
	public void test(ScoreResult score, String value)
	{
		assertEquals(value,score.value);
		assertEquals(Config.INSTANCE.boostString,score.score,0);
	}
	
	@Test public void testChannelOfDeliveryName()
	{		
		Cube cube = Cube.getInstance("finland-aid");
		Scorer scorer = cube.properties.get("http://linkedspending.aksw.org/ontology/finland-aid-channel-of-delivery-name").scorer;
		assertEquals(StringScorer.class,scorer.getClass());
//		test(scorer.score("Finnish Red Cross").get(),"Finnish Red Cross");
		test(scorer.score("Finland Embassy").get(),"Embassy of Finland");
	}
}