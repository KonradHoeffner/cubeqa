package org.aksw.autosparql.cube;

import static org.junit.Assert.*;
import org.aksw.linkedspending.LinkedSpendingDatasetInfo;
import org.junit.Test;
import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.vocabulary.XSD;

public class ComponentPropertyTest
{

//	@Test public void testGuessRange()
//	{
//		fail("Not yet implemented");
//	}

	@Test public void testGetInstance()
	{
		ComponentProperty amount = ComponentProperty.getInstance("http://linkedspending.aksw.org/black-budget", "http://linkedspending.aksw.org/amount", null);
		System.out.println(amount.range);
		System.out.println(amount.labels);

	}

}
