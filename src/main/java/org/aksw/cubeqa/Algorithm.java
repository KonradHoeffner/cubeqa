package org.aksw.cubeqa;

import lombok.extern.log4j.Log4j;
import org.aksw.cubeqa.template.CubeTemplate;
import org.aksw.cubeqa.template.CubeTemplator;

/** Calls the templator which does the main work. */
@Log4j
public class Algorithm
{
	public final Cube cube;

	public Algorithm(String cubeName)
	{
		this.cube = Cube.getInstance(cubeName);
	}

	public CubeTemplate answer(String question)
	{
		CubeTemplate template = new CubeTemplator(cube).buildTemplate(question);
		System.out.println(template.sparqlQuery());
		return template;
	}
}