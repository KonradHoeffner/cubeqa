package org.aksw.autosparql.cube;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import lombok.extern.java.Log;
import org.aksw.autosparql.commons.knowledgebase.DBpediaKnowledgebase;
import org.aksw.autosparql.cube.template.CubeTemplator;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.sparql.engine.http.QueryEngineHTTP;

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
		return new CubeTemplator(cube, question).buildTemplates().sparqlQuery();
	}
}
