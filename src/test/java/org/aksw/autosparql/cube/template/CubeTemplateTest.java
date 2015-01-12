package org.aksw.autosparql.cube.template;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.aksw.autosparql.cube.Aggregate;
import org.aksw.autosparql.cube.Cube;
import org.aksw.autosparql.cube.property.ComponentProperty;
import org.aksw.autosparql.cube.restriction.Restriction;
import org.aksw.autosparql.cube.restriction.UriRestriction;
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
		CubeTemplate ct = new CubeTemplate(cube,restrictions,Collections.singleton(amount),Collections.singleton(Aggregate.SUM));
		ct.perProperties.add(ComponentProperty.getInstance(cube, "http://linkedspending.aksw.org/ontology/finland-aid-reporting-year"));
		System.out.println(ct.sparqlQuery());
	}

}