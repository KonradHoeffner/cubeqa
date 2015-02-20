package org.aksw.autosparql.cube;

import lombok.extern.log4j.Log4j;
import org.aksw.autosparql.cube.template.CubeTemplate;
import org.aksw.autosparql.cube.template.CubeTemplator;

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