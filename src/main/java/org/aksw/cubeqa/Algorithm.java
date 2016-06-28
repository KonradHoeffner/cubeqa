package org.aksw.cubeqa;

import org.aksw.cubeqa.template.GreedyTemplator;
import org.aksw.cubeqa.template.Template;

/** Calls the templator which does the main work. */
public class Algorithm
{

	public Template template(String cubeName, String question)
	{
		Template template = new GreedyTemplator(Cube.getInstance(cubeName)).buildTemplate(question);
		return template;
	}
}