package org.aksw.cubeqa.detector;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.Set;
import org.aksw.cubeqa.Cube;
import org.aksw.cubeqa.restriction.Restriction;
import org.aksw.cubeqa.restriction.ValueRestriction;
import org.aksw.cubeqa.template.Fragment;
import org.junit.jupiter.api.Test;

public class InYearDetectorTest
{
	String[][] q = {
			{"What was the average aid committed per month in year 2010?","in year 2010","2010"},
			{"Which were the top 10 aided countries in 2011?","in 2011","2011"},
			{"How much did the Philippines receive in the year of 2007?","in the year of 2007","2007"}
	};

	@Test public void testDetect()
	{
		for(int i=0;i<q.length;i++)
		{
			Set<Fragment> fragments = InYearDetector.INSTANCE.detect(Cube.finlandAid(),q[i][0]);
			assertEquals(1,fragments.size());
			Fragment f = fragments.stream().filter(ff->ff.getRestrictions().size()==1).findFirst().get();
			Restriction r = f.getRestrictions().iterator().next(); // there should be only one
			assertEquals(ValueRestriction.class,r.getClass()); // temporal restrictions are value restrictions with a filter
			assertTrue(r.getProperty().uri.equals("http://linkedspending.aksw.org/ontology/refYear"));
			assertTrue(r.wherePatterns().iterator().next().matches("(?s).*filter\\(year\\(\\?v[a-z0-9]+\\)="+q[i][2]+".*")); // correct year
			assertEquals(q[i][1],f.getPhrase());	
		}
		
	}

}