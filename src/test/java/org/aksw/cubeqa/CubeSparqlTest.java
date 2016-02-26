package org.aksw.cubeqa;

import static org.junit.Assert.*;
import org.junit.Test;

public class CubeSparqlTest
{

	@Test public void testLinkedSpending()
	{
		CubeSparql sparql = CubeSparql.getLinkedSpendingInstanceForName("black-budget");
		String query = "select count(distinct(?cp)) as ?count {?cp a qb:DimensionProperty}";

		int countBlackBudget = sparql.select(query).nextSolution().get("count").asLiteral().getInt();
		int countAll = CubeSparql.finlandAid().select(query).nextSolution().get("count").asLiteral().getInt();
		assertTrue(countBlackBudget==5);
		assertTrue(countAll>10);
	}

	@Test public void testPageSize()
	{		
		String query = "select ?s {?s ?p ?o.} limit 1000";
		assertEquals(1000,Cube.finlandAid().sparql.select(query).size(),0);
	}

	

}