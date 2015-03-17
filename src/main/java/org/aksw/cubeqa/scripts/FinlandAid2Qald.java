package org.aksw.cubeqa.scripts;

import java.io.IOException;
import org.aksw.cubeqa.benchmark.Benchmark;

public class FinlandAid2Qald
{

	public static void main(String[] args) throws IOException
	{
		Benchmark.fromCsv("finland-aid").saveAsQald();
	}

}