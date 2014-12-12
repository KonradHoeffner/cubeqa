package org.aksw.autosparql.cube.template;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import org.aksw.autosparql.cube.Aggregate;
import org.aksw.autosparql.cube.ComponentProperty;
import org.aksw.autosparql.cube.Cube;
import org.aksw.autosparql.cube.restriction.Restriction;
import org.aksw.autosparql.cube.restriction.UriRestriction;
import org.aksw.autosparql.cube.restriction.ValueRestriction;
import org.junit.Test;

public class CubeTemplateTest
{

	@Test public void testSparqlQuery()
	{
		Cube cube = Cube.getInstance("finland-aid");
		ComponentProperty receipientCountry = cube.properties.get("http://linkedspending.aksw.org/ontology/finland-aid-recipient-country");
		ComponentProperty amount = cube.properties.get("http://linkedspending.aksw.org/ontology/finland-aid-amount");
		Set<Restriction> restrictions = new HashSet<>();

//		ComponentProperty receipientCountry = new ComponentProperty("finland-aid", "http://linkedspending.aksw.org/ontology/finland-aid-recipient-country-spec",null);
		restrictions.add(new UriRestriction(receipientCountry,"https://openspending.org/finland-aid/recipient-country/cn"));
		CubeTemplate ct = new CubeTemplate(cube.uri,restrictions,amount,Optional.of(Aggregate.SUM));
		System.out.println(ct.sparqlQuery());
	}

}
