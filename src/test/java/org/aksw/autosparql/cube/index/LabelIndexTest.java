package org.aksw.autosparql.cube.index;

import static org.junit.Assert.*;
import org.aksw.autosparql.cube.Cube;
import org.aksw.autosparql.cube.property.ComponentProperty;
import org.junit.Test;

public class LabelIndexTest
{

	@Test public void test()
	{
		Cube cube = Cube.getInstance("finland-aid");
		ComponentProperty property = cube.properties.get("http://linkedspending.aksw.org/ontology/finland-aid-recipient-country");
		// TODO score is too high, calculate final score on my own?
		System.out.println(property.scorer.score("Igyppt"));
	}

}