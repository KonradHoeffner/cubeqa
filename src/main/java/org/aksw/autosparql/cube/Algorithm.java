package org.aksw.autosparql.cube;

import lombok.extern.java.Log;
import org.aksw.autosparql.cube.template.CubeTemplator;
import org.aksw.autosparql.cube.template.CubeTemplatorNew;

@Log
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
		return new CubeTemplatorNew(cube, question).buildTemplate().sparqlQuery();
	}
}
