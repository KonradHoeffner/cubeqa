package org.aksw.cubeqa;

import static org.junit.Assert.*;
import java.util.Collections;
import org.aksw.cubeqa.Aggregate;
import org.aksw.cubeqa.AggregateMapping;
import org.junit.Test;

public class AggregateMappingTest
{

	@Test public void testFind()
	{
		assertEquals(AggregateMapping.aggregatesContained("What was the average aid to environment per month in year 2010?"),Collections.singleton(Aggregate.AVG));
		assertEquals(AggregateMapping.aggregatesContained("What is the total biodiversity aid from all sectors for countries with populations greater than 10,000,000?"),Collections.singleton(Aggregate.SUM));
		assertEquals(AggregateMapping.aggregatesContained("How much does Peru receive for drinking water supply a year?"),Collections.EMPTY_SET);
	}
}