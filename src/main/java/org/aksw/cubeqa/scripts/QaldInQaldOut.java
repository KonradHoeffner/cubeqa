package org.aksw.cubeqa.scripts;

import java.io.IOException;
import org.aksw.cubeqa.Cube;
import org.aksw.cubeqa.benchmark.Benchmark;

/**  Test script which reads in qald and saves it back so that we can check if something gets lost.*/
public class QaldInQaldOut
{

	public static void main(String[] args) throws IOException
	{
		Benchmark.fromCsv("finland-aid").saveAsQald(Cube.FINLAND_AID.sparql);
	}

}