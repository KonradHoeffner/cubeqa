package org.aksw.autosparql.cube;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import lombok.extern.java.Log;
import org.aksw.autosparql.commons.knowledgebase.DBpediaKnowledgebase;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.sparql.engine.http.QueryEngineHTTP;

@Log
public class Algorithm
{

	public String answer(String cubeName, String question)
	{
		new CubeTemplator().buildTemplates(Cube.getInstance(cubeName), question);

		return null;
	}
}
