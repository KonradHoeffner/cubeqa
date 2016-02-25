package org.aksw.cubeqa;

import static org.junit.Assert.*;
import org.aksw.cubeqa.template.Template;
import org.junit.Test;
import com.hp.hpl.jena.query.ResultSet;

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
			Template t = new Algorithm().template("finland-aid",question);
			ResultSet rs = t.cube.sparql.select(t.sparqlQuery());
			assertEquals(rs.next().get(rs.getResultVars().get(0)).asLiteral().getInt(),180000);
		}
	}

}