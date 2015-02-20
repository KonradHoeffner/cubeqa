package org.aksw.autosparql.cube.property.scorer;

import static org.junit.Assert.*;
import org.aksw.autosparql.cube.Cube;
import org.junit.Test;

public class ScorersTest
{

	void test(String s)
	{
		System.out.println(Scorers.scorePhraseValues(Cube.FINLAND_AID,s));
	}

	@Test public void testScorePhraseValues()
	{
		// TODO should find new UriRestriction(finprop("sector"),"https://openspending.org/finland-aid/sector/74010")
		test("Finnish Red Cross");
		test("Malaria Control");
		test("Environmental policy and administrative management");
		test("Nepal");
		test("Rescheduling and financing");
		test("education");
		test("Embassy of Finland");
		test("South and Central Asia");
		test("food crop production");

		System.out.println(Scorers.scorePhraseProperties(Cube.FINLAND_AID, "Disaster prevention and preparedness"));
		System.out.println(Scorers.scorePhraseProperties(Cube.FINLAND_AID, "extended amounts"));
		System.out.println(Scorers.scorePhraseValues(Cube.FINLAND_AID, "civil society"));
		System.out.println(Scorers.scorePhraseValues(Cube.FINLAND_AID, "drinking water supply"));

	}

}
