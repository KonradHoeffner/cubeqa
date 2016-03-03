package org.aksw.cubeqa;

import static org.junit.Assert.*;
import static org.aksw.cubeqa.AnswerType.*;
import java.util.EnumSet;
import org.junit.Ignore;
import org.junit.Test;

public class AnswerTypeTest
{
	public void test(String question, EnumSet<AnswerType> types)
	{
		assertEquals(ofQuestion(question),types);
	}

	public void test(String question, AnswerType type)
	{
		assertEquals(ofQuestion(question),EnumSet.of(type));
	}

	@Ignore
	@Test public void testOfQuestion()
	{
		// TODO: recheck all with the table from the paper
		test("How much aid receives a country?",UNCOUNTABLE);
		test("What is the total Dignity International aid from all sectors?",EnumSet.of(UNCOUNTABLE,COUNTABLE, TEMPORAL,LOCATION, ENTITY));
		test("How much money Nepal receives for Environmental policy and administrative management?",UNCOUNTABLE);
		test("Top 10 aid receiving geographic areas in south east and central asia?",EnumSet.of(UNCOUNTABLE,COUNTABLE, TEMPORAL,LOCATION, ENTITY));
		test("Which country has the lowest amount of commitments?",EnumSet.of(TEMPORAL,LOCATION, ENTITY));
		test("How many countries had amounts of more than 1000000 € in 2010?",COUNTABLE);
		test("Where is the biggest aid for medical services?",LOCATION);
		test("When did Paraguy get money from the Finish Red Cross?",TEMPORAL);
		test("Did the Ukraine receive money in 2009?",AFFIRMATIVE);
		test("Was Ethiopia given money for primary education?",AFFIRMATIVE);
	}
}