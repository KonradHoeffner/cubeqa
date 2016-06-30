package org.aksw.cubeqa.scripts;

import org.aksw.cubeqa.Algorithm;
import org.aksw.cubeqa.benchmark.Benchmark;

/** Evaluates the QALD 6 Task 3 train benchmark with 100 questions.
 * @see <a href="http://greententacle.techfak.uni-bielefeld.de/~cunger/qald/index.php?x=challenge&q=6">http://greententacle.techfak.uni-bielefeld.de/~cunger/qald/index.php?x=challenge&q=6</a>. */
public class EvaluateQald6T3Train
{
	public static void main(String[] args)
	{
		long start = System.currentTimeMillis();
		Benchmark.fromQald("qald6t3-train-v1.2").evaluate(new Algorithm());
		System.out.println(System.currentTimeMillis()-start+" ms");
	}
}