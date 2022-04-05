package org.aksw.cubeqa.detector;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.aksw.cubeqa.detector.Aggregate.*;
import org.junit.jupiter.api.Test;

public class AggregateDetectorTest
{
	String[] questions = {
			"What is the total aid to the Anti Corruption Commission in the Maldives in 2015?",
			"What was the average Uganda health budget over all districts in 2014?",
			"What is the average salary of an Engineering Technician in Washington DC?",
			"What was the total budget on Technical Services in City of Toronto in 2009?"
			};

	Aggregate[] aggregates = {SUM,AVG,AVG,SUM};

	@Test public void testDetect()
	{
		for(int i=0; i<questions.length;i ++)
		{
			assertEquals(aggregates[i],AggregateDetector.aggregatesContained(questions[i]).iterator().next());
		}
		
	}

}