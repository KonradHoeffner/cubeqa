package org.aksw.cubeqa.scripts;

import org.aksw.cubeqa.Algorithm;
import org.aksw.cubeqa.benchmark.Benchmark;

public class EvaluateQald
{

	public static void main(String[] args)
	{
		Benchmark.fromQald("finland-aid").evaluate(new Algorithm("finland-aid"),1);
	}
}