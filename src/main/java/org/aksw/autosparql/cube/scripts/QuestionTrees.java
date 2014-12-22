//package org.aksw.autosparql.cube.scripts;
//
//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.InputStreamReader;
//import org.aksw.autosparql.cube.template.CubeTemplator;
//
//public class QuestionTrees
//{
//
//	public static void main(String[] args) throws IOException
//	{
//		CubeTemplator templator = new CubeTemplator();
//		try(BufferedReader in = new BufferedReader(new InputStreamReader(QuestionTrees.class.getClassLoader().getResourceAsStream("questions.txt"))))
//		{
//			String line;
//			while((line=in.readLine())!=null)
//			{
//				templator.buildTemplates(null, line);
//			}
//		}
//
//	}
//
//}
