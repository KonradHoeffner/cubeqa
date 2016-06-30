package org.aksw.cubeqa;

import org.aksw.cubeqa.template.Template;
import org.aksw.cubeqa.template.WeightedTemplator;

/** Calls the templator which does the main work. */
public class Algorithm
{

	public Template template(String cubeName, String question)
	{
		Template template = new WeightedTemplator(Cube.getInstance(cubeName)).buildTemplate(question);
		return template;
	}
}