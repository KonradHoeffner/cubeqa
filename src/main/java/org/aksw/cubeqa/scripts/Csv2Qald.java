package org.aksw.cubeqa.scripts;

import java.io.IOException;
import java.util.Scanner;
import org.aksw.cubeqa.benchmark.Benchmark;

public class Csv2Qald
{
	public static void main(String[] args) throws IOException
	{
		System.out.println("type csv2qald to continue");
		if(!new Scanner(System.in).nextLine().equals("csv2qald")) {System.out.println("wrong phrase. terminated.");return;}
		Benchmark.fromCsv("qbench2").saveAsQald();
	}
}