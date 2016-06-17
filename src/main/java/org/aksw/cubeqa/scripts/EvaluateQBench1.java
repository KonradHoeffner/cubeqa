package org.aksw.cubeqa.scripts;

import org.aksw.cubeqa.Algorithm;
import org.aksw.cubeqa.benchmark.Benchmark;

/** Evaluates the old single-dataset benchmark on finland-aid. */
public class EvaluateQBench1
{
	public static void main(String[] args)
	{
		long start = System.currentTimeMillis();
		Benchmark.fromQald("finland-aid").evaluate(new Algorithm(),6);
		System.out.println(System.currentTimeMillis()-start+" ms");
	}
}