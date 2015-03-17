package org.aksw.cubeqa.scripts;

import java.io.File;
import java.io.IOException;
import org.aksw.cubeqa.benchmark.Benchmark;

public class FinlandAid2Qald
{

	public static void main(String[] args) throws IOException
	{
		Benchmark.fromCsv(new File("benchmark/finland-aid.csv")).saveAsQald(new File("benchmark/finland-aid.xml"), "qald-6_datacube");
	}

}