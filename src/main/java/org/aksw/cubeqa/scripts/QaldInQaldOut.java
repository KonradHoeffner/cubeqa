package org.aksw.cubeqa.scripts;

import java.io.File;
import java.io.IOException;

import org.aksw.cubeqa.Files;
import org.aksw.cubeqa.benchmark.Benchmark;

/**  Test script which reads in qald and saves it back so that we can check if something gets lost.*/
public class QaldInQaldOut
{

	public static void main(String[] args) throws IOException
	{
		Benchmark.fromQald("qald6t3-train-v1.2").saveAsQald(new File(Files.localFolder("benchmark"),"test.xml"));
	}

}