package org.aksw.autosparql.cube;

import static org.junit.Assert.*;
import org.junit.Test;

public class CubeSparqlTest
{

	@Test public void testLinkedSpending()
	{
		CubeSparql sparql = CubeSparql.linkedSpending("black-budget");
		String query = "select count(distinct(?cp)) as ?count {?cp a qb:DimensionProperty}";

		int countBlackBudget = sparql.select(query).nextSolution().get("count").asLiteral().getInt();
		int countAll = CubeSparql.LINKED_SPENDING.select(query).nextSolution().get("count").asLiteral().getInt();

		assertTrue(countBlackBudget==5);
		assertTrue(countAll>10);
	}

}