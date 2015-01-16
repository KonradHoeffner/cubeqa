package org.aksw.autosparql.cube;

import org.apache.lucene.search.spell.NGramDistance;
import org.junit.Test;

public class ComponentPropertyTest
{

	@Test public void testMatch()
	{
//		ComponentProperty amount = ComponentProperty.getInstance(Cube.getInstance("finland-aid"), "http://linkedspending.aksw.org/ontology/finland-aid-amounts-extended");
//		System.out.println(amount.range);
//		System.out.println(amount.labels);
//		System.out.println(amount.match("extended amounts"));
		System.out.println(new NGramDistance().getDistance("Amounts extended", "extended amounts"));

//		ComponentProperty amount = ComponentProperty.getInstance(Cube.getInstance("finland-aid"), "http://linkedspending.aksw.org/amount");

	}

//	@Test public void testGetInstance()
//	{
//		ComponentProperty amount = ComponentProperty.getInstance(Cube.getInstance("finland-aid"), "http://linkedspending.aksw.org/ontology/finland-aid-amounts-extended");
//		System.out.println(amount.range);
//		System.out.println(amount.labels);
//
//	}

}
