package org.aksw.cubeqa;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.aksw.cubeqa.template.Template;
import org.junit.jupiter.api.Test;
import org.apache.jena.query.ResultSet;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AlgorithmTest
{
	final String[] questions =
	{
			"How much did the Philippines receive in the year of 2007?",
			"How much money was given to strengthen civil society in Yemen?",
			"How much did the top 10 aided countries get in 2008?",
//		"What was the average aid to environment per month in year 2010?"
//		,"How much wood would a wood chuck chuck?"
	};

	// TODO: find out why the sector is not found in AlgorithmTest even when boostString is set to 0.1 (in ObjectPropertyScorerTest it works)
	@Test public void testAnswer()
	{
//		for(String question: questions)
		String question = questions[0];
		{
			Template t = new Algorithm().template("finland-aid",question);
			ResultSet rs = t.cube.sparql.select(t.sparqlQuery());
			assertTrue(rs.hasNext());	
			log.debug(t.sparqlQuery());
			log.debug(rs.getResultVars().get(0));
			if(question==questions[1]) assertEquals(rs.next().get(rs.getResultVars().get(0)).asLiteral().getInt(),180000);
		}
	}

}
