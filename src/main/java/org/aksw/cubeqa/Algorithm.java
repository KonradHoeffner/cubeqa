package org.aksw.cubeqa;

import org.aksw.cubeqa.template.CubeTemplate;
import org.aksw.cubeqa.template.CubeTemplator;

/** Calls the templator which does the main work. */
public class Algorithm
{

	public CubeTemplate answer(String cubeName, String question)
	{
		CubeTemplate template = new CubeTemplator(Cube.getInstance(cubeName)).buildTemplate(question);
		return template;
	}
}