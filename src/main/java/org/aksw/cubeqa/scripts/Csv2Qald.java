package org.aksw.cubeqa.scripts;

import java.util.Scanner;
import java.io.IOException;
import org.aksw.cubeqa.benchmark.Benchmark;

public class Csv2Qald
{
	public static void main(String[] args) throws IOException
	{
		System.out.println("type csv2qald to continue");
		try(Scanner in = new Scanner(System.in))
		{if(!in.nextLine().equals("csv2qald")) {System.out.println("wrong phrase. terminated.");return;}}
		System.out.println("Converting csv to QALD xml format and generating answers from SPARQL endpoint. This may take a while.");
		Benchmark.fromCsv("qald6t3-train-v1.1").saveAsQald();
	}
}