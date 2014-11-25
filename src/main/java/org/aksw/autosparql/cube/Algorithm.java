package org.aksw.autosparql.cube;

public class Algorithm
{
	public static String answer(String cubeName, String question)
	{
		Cube c = Cube.getInstance(cubeName);
//		new CubeTemplator().buildTemplates(question);
		// parse question

		System.out.println(c);
		return null;
	}
}
