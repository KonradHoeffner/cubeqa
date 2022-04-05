package org.aksw.cubeqa.detector;

import static org.junit.jupiter.api.Assertions.*;
import java.util.Set;
import org.aksw.cubeqa.Cube;
import org.aksw.cubeqa.restriction.Restriction;
import org.aksw.cubeqa.restriction.UriRestriction;
import org.aksw.cubeqa.template.Fragment;
import org.junit.jupiter.api.Test;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class InPlaceDetectorTest
{
	// TODO: add multi word places like "the United States of America".
	String[][] q = {
			{"How much money was given to strengthen civil society in Yemen?","in Yemen","https://openspending.org/finland-aid/recipient-country/ye"},

	};

	@Test public void testDetect()
	{
		for(int i=0;i<q.length;i++)
		{
			Set<Fragment> fragments = InPlaceDetector.INSTANCE.detect(Cube.finlandAid(),q[i][0]);
			log.debug("{}",fragments);
			assertEquals(1,fragments.size());
			Fragment f = fragments.stream().filter(ff->ff.getRestrictions().size()==1).findFirst().get();
			Restriction r = f.getRestrictions().iterator().next(); // there should be only one
			assertEquals(UriRestriction.class,r.getClass()); // temporal restrictions are value restrictions with a filter
			assertTrue(r.getProperty().uri.equals("http://linkedspending.aksw.org/ontology/finland-aid-recipient-country"));
			assertTrue(r.wherePatterns().iterator().next().contains("<https://openspending.org/finland-aid/recipient-country/ye>"));
			assertEquals(q[i][1],f.getPhrase());	
		}

	}

}