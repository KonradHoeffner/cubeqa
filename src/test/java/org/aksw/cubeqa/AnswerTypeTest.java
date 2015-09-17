package org.aksw.cubeqa;

import static org.junit.Assert.*;
import java.util.EnumSet;
import org.junit.Test;

public class AnswerTypeTest
{
	public void test(String question, EnumSet<AnswerType> types)
	{
		assertEquals(AnswerType.ofQuestion(question),types);
	}

	public void test(String question, AnswerType type)
	{
		assertEquals(AnswerType.ofQuestion(question),EnumSet.of(type));
	}

	@Test public void testOfQuestion()
	{
		test("How much aid receives a country?",AnswerType.UNCOUNTABLE);
		test("What is the total Dignity International aid from all sectors?",EnumSet.allOf(AnswerType.class));
		test("How much money Nepal receives for Environmental policy and administrative management?",AnswerType.UNCOUNTABLE);
		test("Top 10 aid receiving geographic areas in south east and central asia?",EnumSet.allOf(AnswerType.class));
		test("Which country has the lowest amount of commitments?",EnumSet.allOf(AnswerType.class));
		test("How many countries had amounts of more than 1000000 € in 2010?",AnswerType.COUNTABLE);
		test("Where is the biggest aid for medical services?",AnswerType.LOCATION);
		test("When did Paraguy get money from the Finish Red Cross?",AnswerType.TEMPORAL);
		test("Did the Ukraine receive money in 2009?",AnswerType.AFFIRMATIVE);
		test("Was Ethiopia given money for primary education?",AnswerType.AFFIRMATIVE);
	}
}