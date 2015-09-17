package org.aksw.cubeqa;

import static org.junit.Assert.assertTrue;
import org.junit.Test;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.sparql.engine.http.QueryEngineHTTP;

public class JenaNanBugTest
{

	@Test public void test()
	{
		String query ="select (min(xsd:double(?d)) as ?min) "
				+ "{?o a qb:Observation. ?o qb:dataSet <http://linkedspending.aksw.org/instance/finland-aid>.?o <http://linkedspending.aksw.org/ontology/finland-aid-aid-to-environment> ?d.} limit 1";
		try(QueryEngineHTTP qe = new QueryEngineHTTP("http://linkedspending.aksw.org/sparql", query))
		{
			ResultSet rs = qe.execSelect();
			QuerySolution qs = rs.nextSolution();
			assertTrue(qs.get("min").asLiteral().getDouble()==0);
		}
	}

}