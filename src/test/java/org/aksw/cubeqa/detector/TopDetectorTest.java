package org.aksw.cubeqa.detector;

import static org.junit.Assert.*;
import java.util.regex.Pattern;
import org.aksw.cubeqa.Cube;
import org.aksw.cubeqa.restriction.Restriction;
import org.junit.Test;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TopDetectorTest
{
	/** uses CubeTemplateFragment.toString to test more easily, may break if CubeTemplateFragment.toString changes*/
	@Test public void testDetect10HighestAmounts()
	{
		Cube cube = Cube.finlandAid();
		{
			Restriction r = TopDetector.INSTANCE.detect(cube,"10 highest amounts").iterator().next().getRestrictions().iterator().next();
			log.debug("{}",r);
			assertTrue(Pattern.matches("(?i).*order by DESC\\(.*\\) limit 10.*", r.orderLimitPatterns().iterator().next()));
			assertEquals("http://linkedspending.aksw.org/ontology/finland-aid-amount",r.getProperty().uri);			
		}
	}
		
	@Test public void testDetect5LowAmounts()
	{
		Cube cube = Cube.finlandAid();
		{
			Restriction r = TopDetector.INSTANCE.detect(cube,"5 lowest amounts").iterator().next().getRestrictions().iterator().next();
			log.debug("{}",r);
			assertTrue(Pattern.matches("(?i).*order by ASC\\(.*\\) limit 5.*", r.orderLimitPatterns().iterator().next()));
			assertEquals("http://linkedspending.aksw.org/ontology/finland-aid-amount",r.getProperty().uri);			
		}
	}
	
		@Test public void testDetectTop10AidedCountries()
		{
			Cube cube = Cube.finlandAid();
			{
				Restriction r = TopDetector.INSTANCE.detect(cube,"top 10 aided countries").iterator().next().getRestrictions().iterator().next();
				log.debug("{}",r);
				assertTrue(Pattern.matches("(?i).*order by DESC\\(.*\\) limit 10.*", r.orderLimitPatterns().iterator().next()));
				assertEquals("http://linkedspending.aksw.org/ontology/finland-aid-amount",r.getProperty().uri);			
			}
	}		
//	{
//	String ds = TopDetector.INSTANCE.detect(cube,"top 5 amounts").toString();
//	assertTrue(Pattern.matches("(?i).*order by DESC\\(\\?v[0-9]+\\) limit 5.*", ds));
//	assertTrue(ds.contains("http://linkedspending.aksw.org/ontology/finland-aid-amount "));
//}
//{
//	String ds = TopDetector.INSTANCE.detect(cube,"7 lowest extended amounts").toString();
//	assertTrue(Pattern.matches("(?i).*order by ASC\\(\\?v[0-9]+\\) limit 7.*", ds));
//	assertTrue(ds.contains("http://linkedspending.aksw.org/ontology/finland-aid-amounts-extended "));
//}

}