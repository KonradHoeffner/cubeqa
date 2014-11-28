package org.aksw.autosparql.cube;

import static org.junit.Assert.*;
import org.junit.Test;

public class AlgorithmTest
{
	final String question = "What is the amount in 2012 in the European Union?";

	@Test public void testAnswer()
	{
		new Algorithm().answer("black-budget", question);
	}

}
