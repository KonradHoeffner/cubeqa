package org.aksw.cubeqa.property.scorer;

import java.util.Comparator;
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

	/** @param shouldBe either a uri (objectproperty) or a label lexical form (datatypeproperty) */
	void test(String s,String shouldBe)
	{
		Map<ComponentProperty, ScoreResult> m = Scorers.scorePhraseValues(Cube.FINLAND_AID,s);
		assertEquals(m.values().stream().max(Comparator.comparing(ScoreResult::getScore)).get().value,shouldBe);
	}

	@Test public void testScorePhraseValues()
	{
		test("on health education","https://openspending.org/finland-aid/sector/12261");
		test("Finnish Red Cross","Finnish Red Cross");
		test("Malaria Control","https://openspending.org/finland-aid/sector/12262");
		test("Environmental policy and administrative management");
		test("Nepal","https://openspending.org/finland-aid/recipient-country/np");
		test("Rescheduling and financing","https://openspending.org/finland-aid/sector/60040");
		test("Finland Embassy","Embassy of Finland");
	}
}