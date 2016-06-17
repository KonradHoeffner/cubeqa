package org.aksw.cubeqa.scripts;

import org.aksw.cubeqa.Algorithm;
import org.aksw.cubeqa.benchmark.Benchmark;

/** Evaluates the QALD 6 Task 3 test benchmark with 50 questions.
 * The test questions will be available at the following URI from April 8, 2016 and will then be checked into the repository as well.
 * @see <a href="http://greententacle.techfak.uni-bielefeld.de/~cunger/qald/index.php?x=challenge&q=6">http://greententacle.techfak.uni-bielefeld.de/~cunger/qald/index.php?x=challenge&q=6</a>. */
public class EvaluateQald6T3Test
{
	public static void main(String[] args)
	{
		long start = System.currentTimeMillis();
		Benchmark.fromQald("qald6t3-test").evaluate(new Algorithm());
		System.out.println(System.currentTimeMillis()-start+" ms");
	}
}