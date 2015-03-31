package org.aksw.cubeqa;

import org.aksw.cubeqa.Algorithm;
import org.junit.Test;

public class AlgorithmTest
{
	final String[] questions =
	{
			"How much money was given to strengthen civil society in Yemen?",
//		"What was the average aid to environment per month in year 2010?"
//		,"How much wood would a wood chuck chuck?"
	};

	@Test public void testAnswer()
	{
		for(String question: questions)
		{
			new Algorithm("finland-aid").answer(question);
		}
	}

}