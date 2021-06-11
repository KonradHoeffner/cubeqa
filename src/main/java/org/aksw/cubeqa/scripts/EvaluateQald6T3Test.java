package org.aksw.cubeqa.scripts;

import java.io.IOException;

import org.aksw.cubeqa.Algorithm;
import org.aksw.cubeqa.benchmark.Benchmark;

/** Evaluates the QALD 6 Task 3 test benchmark with 50 questions.
 * @see <a href="https://qald.aksw.org/index.php?x=challenge&q=6">https://qald.aksw.org/index.php?x=challenge&q=6</a>. */
public class EvaluateQald6T3Test
{
	public static void main(String[] args) throws IOException
	{
		long start = System.currentTimeMillis();
		Benchmark.fromJson("qald6t3-test").evaluate(new Algorithm());
		System.out.println(System.currentTimeMillis()-start+" ms");
	}
}