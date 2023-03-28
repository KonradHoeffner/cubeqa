package org.aksw.cubeqa;

import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.sparql.exec.http.QueryExecutionHTTP;

public class JenaNanBugTest
{

	@Test public void test()
	{
		final String PREFIXES = "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> PREFIX qb: <http://purl.org/linked-data/cube#>";
		final String query = PREFIXES+" select (min(xsd:double(?d)) as ?min) "
				+ "{?o a qb:Observation. ?o qb:dataSet <http://linkedspending.aksw.org/instance/finland-aid>.?o <http://linkedspending.aksw.org/ontology/finland-aid-aid-to-environment> ?d.} limit 1";
		try(QueryExecutionHTTP qe = QueryExecutionHTTP.service(CubeSparqlEndpoint.ENDPOINT_LS, query))
		{
			ResultSet rs = qe.execSelect();
			QuerySolution qs = rs.nextSolution();
			assertTrue(qs.get("min").asLiteral().getDouble()==0);
		}
	}

}