package org.aksw.cubeqa;

import org.aksw.cubeqa.Algorithm;
import org.junit.Test;

public class AlgorithmTest
{
	final String[] questions =
	{
			"What is the total biodiversity aid from all sectors for countries with populations greater than 10,000,000?",
//		"What was the average aid to environment per month in year 2010?"
//		,"How much wood would a wood chuck chuck?"
	};

	@Test public void testAnswer()
	{
		for(String question: questions)
		{
			new Algorithm("finnland-aid").answer(question);
		}
	}

}