package org.aksw.autosparql.cube;

import org.aksw.autosparql.cube.property.ComponentProperty;
import org.apache.lucene.search.spell.NGramDistance;
import org.junit.Test;

public class ComponentPropertyTest
{

	@Test public void testMatch()
	{
		ComponentProperty ea = ComponentProperty.getInstance(Cube.FINLAND_AID, "http://linkedspending.aksw.org/ontology/finland-aid-amounts-extended");
		ComponentProperty a = ComponentProperty.getInstance(Cube.FINLAND_AID, "http://linkedspending.aksw.org/ontology/finland-aid-amount");

		System.out.println(ea.match("extended amounts"));
		System.out.println(a.match("extended amounts"));
	}

//	@Test public void testGetInstance()
//	{
//		ComponentProperty amount = ComponentProperty.getInstance(Cube.getInstance("finland-aid"), "http://linkedspending.aksw.org/ontology/finland-aid-amounts-extended");
//		System.out.println(amount.range);
//		System.out.println(amount.labels);
//
//	}

}