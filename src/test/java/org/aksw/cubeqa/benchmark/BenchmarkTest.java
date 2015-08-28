package org.aksw.cubeqa.benchmark;

import static org.junit.Assert.*;
import java.io.File;
import java.io.IOException;
import java.util.*;
import org.aksw.cubeqa.Algorithm;
import org.aksw.cubeqa.CubeSparql;
import org.junit.Test;

public class BenchmarkTest
{
	@Test public void testCompleteQuestion()
	{
		Question q = Benchmark.completeQuestion(CubeSparql.FINLAND_AID, "some string","ask {?s ?p ?o.}");
		assertTrue(q.answers.size()==1);
		assertTrue(q.answerTypes.get("")==DataType.BOOLEAN);
		assertTrue(q.answers.iterator().next().get("").equals("true"));
	}

	@Test public void testEvaluate()
	{
		Benchmark.fromQald("finland-aid").evaluate(new Algorithm("finland-aid"),0);
	}

	@Test public void testFromCsv()
	{
		fail("Not yet implemented");
	}

	@Test public void testNodeString()
	{
		fail("Not yet implemented");
	}

	@Test public void testFromQald()
	{
		Benchmark b = Benchmark.fromQald("finland-aid");
		assertTrue(b.questions.size()==100);
		assertTrue(b.questions.get(0).string.equals("What was the average aid to environment per month in year 2010?"));
		assertTrue(b.questions.get(0).answerTypes.get("")==DataType.NUMBER);
		assertTrue(b.questions.get(0).answers.iterator().next().get("").toString().startsWith("262.6"));
	}

	@Test public void testSaveAndLoadQald() throws IOException
	{
		Benchmark b = Benchmark.fromQald("finland-aid");
		b.saveAsQald(new File(new File("benchmark"),"test.xml"));
		Benchmark c = Benchmark.fromQald("test");
		for(int i=0;i<100;i++)
		{
			Question q = b.questions.get(i);
			Question r = c.questions.get(i);
			// to get more targeted debug output in case of inequalities
			assertEquals(q.string,r.string);
			assertEquals(q.query,r.query);
			assertEquals(q.answerTypes,r.answerTypes);
			assertEquals(q.answers,r.answers);
			assertEquals(q,r);
		}
	}

}