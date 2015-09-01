package org.aksw.cubeqa;

import static org.junit.Assert.*;
import org.aksw.cubeqa.CubeSparql;
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

}