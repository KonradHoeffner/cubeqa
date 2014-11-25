package org.aksw.autosparql.cube;

import static org.junit.Assert.*;
import org.junit.Test;

public class AlgorithmTest
{
	final String question = "What is the amount of 2012?";

	@Test public void testAnswer()
	{
		Algorithm.answer("black-budget", question);
	}

}
