package org.aksw.cubeqa;

import static org.junit.Assert.*;
import java.util.Set;
import java.util.stream.Collectors;
import org.aksw.cubeqa.property.ComponentProperty;
import org.junit.Ignore;
import org.junit.Test;
import org.simmetrics.StringMetric;
import org.simmetrics.metrics.StringMetrics;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ComponentPropertyTest
{
	@Test public void testVar()
	{
		Cube c = Cube.finlandAid();
		Set<String> vars = c.properties.values().stream().map(p->p.var).collect(Collectors.toSet());
		for(ComponentProperty p: c.properties.values()) {log.debug(p.var+" "+p.uri);}
		log.debug(Cube.finlandAid().properties.get("http://linkedspending.aksw.org/ontology/finland-aid-amount").var);
		log.debug("{}",vars);
		assertEquals(vars.size(),c.properties.size());
	}

	@Test public void testMatch()
	{
		{
			ComponentProperty rc = ComponentProperty.getInstance(Cube.finlandAid(), "http://linkedspending.aksw.org/ontology/finland-aid-recipient-country");
			log.debug("{}",rc.match("country"));
			assertTrue(rc.match("country")>=0.4);
		}
		{
			ComponentProperty ea = ComponentProperty.getInstance(Cube.finlandAid(), "http://linkedspending.aksw.org/ontology/finland-aid-amounts-extended");
			log.debug("{}",ea.match("amounts extended"));
			assertTrue(ea.match("amounts extended")>=0.9);
			log.debug("{}",ea.match("extended amounts"));
			assertTrue(ea.match("extended amounts")>=0.5);
		}
		{
			ComponentProperty a = ComponentProperty.getInstance(Cube.finlandAid(), "http://linkedspending.aksw.org/ontology/finland-aid-amount");
			log.debug("{}",a.match("extended amounts"));
			assertTrue(a.match("extended amounts")<0.4);
			log.debug("{}",a.match("amounts"));
			assertTrue(a.match("amounts")>0.6);
		}
	}

	@Ignore
	@Test public void testDistances()
	{
		StringMetric similarity = StringMetrics.qGramsDistance();
		System.out.println(similarity.compare("amountsextended","amountsextended"));
		System.out.println(similarity.compare("amounts extended","extended amounts"));
		System.out.println(similarity.compare("amountsextended","extendedamounts"));
		System.out.println(similarity.compare("nestle","nestlé"));
		System.out.println(similarity.compare("nerf","berg"));
	}

	@Test public void testRanges()
	{
		Cube c = Cube.finlandAid();
		//		log.debug(c.properties.values());
		Set<String> ranges = c.properties.values().stream().map(p->p.range).collect(Collectors.toSet());
		assertTrue(ranges.contains("http://www.w3.org/2001/XMLSchema#date"));
		assertTrue(ranges.contains("http://www.w3.org/2001/XMLSchema#string"));
		assertTrue(ranges.contains("http://www.w3.org/2001/XMLSchema#gYear"));
		//		log.debug(ranges);
	}

	//	@Test public void testGetInstance()
	//	{
	//		ComponentProperty amount = ComponentProperty.getInstance(Cube.getInstance("finland-aid"), "http://linkedspending.aksw.org/ontology/finland-aid-amounts-extended");
	//		log.debug(amount.range);
	//		log.debug(amount.labels);
	//
	//	}

}