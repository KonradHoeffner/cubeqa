package org.aksw.cubeqa;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

public class CubeSparqlTest
{

	@Test public void testLinkedSpending()
	{
		CubeSparql sparql = CubeSparql.getLinkedSpendingInstanceForName("finland-aid");
		String query = "SELECT (COUNT(DISTINCT(?dim)) AS ?count) {?dim a qb:DimensionProperty}";
		int dimensions = sparql.select(query).nextSolution().get("count").asLiteral().getInt();
		assertEquals(4,dimensions);
	}

	@Test public void testPageSize()
	{		
		String query = "select ?s {?s ?p ?o.} limit 1000";
		assertEquals(1000,Cube.finlandAid().sparql.select(query).size(),0);
	}

	
}