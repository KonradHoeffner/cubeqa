package org.aksw.cubeqa;

import static org.junit.Assert.*;
import java.util.Set;
import java.util.stream.Collectors;
import org.aksw.cubeqa.property.ComponentProperty;
import org.junit.Test;

public class ComponentPropertyTest
{
	@Test public void testVar()
	{
		Cube c = Cube.finlandAid();
		Set<String> vars = c.properties.values().stream().map(p->p.var).collect(Collectors.toSet());
		for(ComponentProperty p: c.properties.values()) System.out.println(p.var+" "+p.uri);
		System.out.println(Cube.finlandAid().properties.get("http://linkedspending.aksw.org/ontology/finland-aid-amount").var);
		System.out.println(vars);
		assertEquals(vars.size(),c.properties.size());
	}

	@Test public void testMatch()
	{
		ComponentProperty rc = ComponentProperty.getInstance(Cube.finlandAid(), "http://linkedspending.aksw.org/ontology/finland-aid-recipient-country");
		System.out.println(rc.match("country"));
		ComponentProperty ea = ComponentProperty.getInstance(Cube.finlandAid(), "http://linkedspending.aksw.org/ontology/finland-aid-amounts-extended");
		ComponentProperty a = ComponentProperty.getInstance(Cube.finlandAid(), "http://linkedspending.aksw.org/ontology/finland-aid-amount");

		System.out.println(ea.match("extended amounts"));
		System.out.println(a.match("extended amounts"));
	}

	@Test public void testRanges()
	{
		Cube c = Cube.finlandAid();
//		System.out.println(c.properties.values());
		Set<String> ranges = c.properties.values().stream().map(p->p.range).collect(Collectors.toSet());
		assertTrue(ranges.contains("http://www.w3.org/2001/XMLSchema#date"));
		assertTrue(ranges.contains("http://www.w3.org/2001/XMLSchema#string"));
		assertTrue(ranges.contains("http://www.w3.org/2001/XMLSchema#gYear"));
//		System.out.println(ranges);
	}

//	@Test public void testGetInstance()
//	{
//		ComponentProperty amount = ComponentProperty.getInstance(Cube.getInstance("finland-aid"), "http://linkedspending.aksw.org/ontology/finland-aid-amounts-extended");
//		System.out.println(amount.range);
//		System.out.println(amount.labels);
//
//	}

}