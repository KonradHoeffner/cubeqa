package org.aksw.cubeqa;

import org.aksw.cubeqa.template.Template;
import org.aksw.cubeqa.template.Templator;

/** Calls the templator which does the main work. */
public class Algorithm
{

	public Template answer(String cubeName, String question)
	{
		Template template = new Templator(Cube.getInstance(cubeName)).buildTemplate(question);
		return template;
	}
}