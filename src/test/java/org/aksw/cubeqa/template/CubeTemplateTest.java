package org.aksw.cubeqa.template;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.aksw.cubeqa.Cube;
import org.aksw.cubeqa.detector.Aggregate;
import org.aksw.cubeqa.property.ComponentProperty;
import org.aksw.cubeqa.restriction.Restriction;
import org.aksw.cubeqa.restriction.UriRestriction;
import org.aksw.cubeqa.template.CubeTemplate;
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
		CubeTemplate ct = new CubeTemplate(cube,restrictions,Collections.singleton(amount),Collections.emptySet(),Collections.singleton(Aggregate.SUM));
		ct.perProperties.add(ComponentProperty.getInstance(cube, "http://linkedspending.aksw.org/ontology/finland-aid-reporting-year"));
		System.out.println(ct.sparqlQuery());
	}

}