package org.aksw.cubeqa.property.scorer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.util.Collection;
import java.util.Comparator;
import org.aksw.cubeqa.Cube;
import org.junit.jupiter.api.Test;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ScorersTest
{
	private void test(String s)
	{
		log.debug("{}",Scorers.scorePhraseProperties(Cube.finlandAid(),s));
	}

	/** @param expected either a uri (objectproperty) or a label lexical form (datatypeproperty) */
	void test(String s,String expected)
	{
		Collection<ScoreResult> scores = Scorers.scorePhraseValues(Cube.finlandAid(),s).values();
		assertEquals(expected,scores.stream().max(Comparator.comparing(ScoreResult::getScore)).get().value);
	}

	@Test public void testScorePhraseValues()
	{
//		test("on health education","https://openspending.org/finland-aid/sector/12261");
		test("Finnish Red Cross","Finnish Red Cross");
		test("Malaria Control","https://openspending.org/finland-aid/sector/12262");
		test("Environmental policy and administrative management");
		test("Nepal","https://openspending.org/finland-aid/recipient-country/np");
		test("Rescheduling and financing","https://openspending.org/finland-aid/sector/60040");
		test("Finland Embassy","Embassy of Finland");		
	}
}