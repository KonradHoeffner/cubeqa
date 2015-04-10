package org.aksw.cubeqa.property.scorer;

import java.util.Map;
import org.aksw.cubeqa.Cube;
import org.aksw.cubeqa.property.ComponentProperty;
import org.junit.Test;
import static org.junit.Assert.*;

public class ScorersTest
{
	void test(String s)
	{
		System.out.println(Scorers.scorePhraseValues(Cube.FINLAND_AID,s));
	}

	void test(String s,String shouldBe)
	{
		Map<ComponentProperty, ScoreResult> m = Scorers.scorePhraseValues(Cube.FINLAND_AID,s);
		System.out.println(m);
		assertTrue(m.values().iterator().next().getValue().equals(shouldBe));
	}

	@Test public void testScorePhraseValues()
	{
		// TODO should find new UriRestriction(finprop("sector"),"https://openspending.org/finland-aid/sector/74010")
		System.out.println(Cube.FINLAND_AID.properties.get("http://linkedspending.aksw.org/ontology/finland-aid-sector").scorer.score("health education"));
//		test("on health education");
//		test("Finnish Red Cross","Finnish Red Cross");
//		test("Malaria Control");
//		test("Environmental policy and administrative management");
//		test("Nepal");
//		test("Rescheduling and financing");
//		test("education");
//		test("Embassy of Finland");
//		test("South and Central Asia");
//		test("food crop production");
//
//		System.out.println(Scorers.scorePhraseProperties(Cube.FINLAND_AID, "Disaster prevention and preparedness"));
//		System.out.println(Scorers.scorePhraseProperties(Cube.FINLAND_AID, "extended amounts"));
//		System.out.println(Scorers.scorePhraseValues(Cube.FINLAND_AID, "civil society"));
//		System.out.println(Scorers.scorePhraseValues(Cube.FINLAND_AID, "drinking water supply"));

	}

}
