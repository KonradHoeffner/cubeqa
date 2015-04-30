package org.aksw.cubeqa.scripts;

import org.aksw.cubeqa.Algorithm;
import org.aksw.cubeqa.benchmark.Benchmark;

public class EvaluateQald
{
	public static void main(String[] args)
	{
		long start = System.currentTimeMillis();
		Benchmark.fromQald("finland-aid").evaluate(new Algorithm("finland-aid"));
		System.out.println(System.currentTimeMillis()-start+" ms");
	}
}