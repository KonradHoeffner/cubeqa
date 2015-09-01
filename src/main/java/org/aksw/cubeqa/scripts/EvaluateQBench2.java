package org.aksw.cubeqa.scripts;

import org.aksw.cubeqa.Algorithm;
import org.aksw.cubeqa.benchmark.Benchmark;

/** Evaluates the second benchmark, which has 100 questions refering to 50 different datasets.
 * Each question refers to only one dataset however.
 * Questions that need a combination of multiple datasets and the algorithm that handles that are future work. */
public class EvaluateQBench2
{
	public static void main(String[] args)
	{
		long start = System.currentTimeMillis();
		Benchmark.fromQald("qbench2").evaluate(new Algorithm());
		System.out.println(System.currentTimeMillis()-start+" ms");
	}
}