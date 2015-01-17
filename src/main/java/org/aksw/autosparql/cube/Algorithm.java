package org.aksw.autosparql.cube;

import lombok.extern.java.Log;
import lombok.extern.log4j.Log4j;
import org.aksw.autosparql.cube.template.CubeTemplator;
import org.aksw.autosparql.cube.template.CubeTemplator;

@Log4j
public class Algorithm
{
	public final Cube cube;

	public Algorithm(String cubeName)
	{
		this.cube = Cube.getInstance(cubeName);
	}

	public String answer(String question)
	{
		log.info("Answering "+question+"on cube "+cube+"...");
		return new CubeTemplator(cube).buildTemplate(question).sparqlQuery();
	}
}