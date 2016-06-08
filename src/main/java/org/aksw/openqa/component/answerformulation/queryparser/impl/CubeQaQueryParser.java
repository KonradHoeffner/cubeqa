package org.aksw.openqa.component.answerformulation.queryparser.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.aksw.cubeqa.Algorithm;
import org.aksw.cubeqa.Config;
import org.aksw.cubeqa.Cube;
import org.aksw.cubeqa.index.CubeIndex;
import org.aksw.openqa.Properties;
import org.aksw.openqa.component.answerformulation.AbstractQueryParser;
import org.aksw.openqa.component.context.IContext;
import org.aksw.openqa.component.param.IParamMap;
import org.aksw.openqa.component.param.IResultMap;
import org.aksw.openqa.component.param.ResultMap;
import org.aksw.openqa.component.providers.impl.ServiceProvider;
import org.aksw.openqa.component.service.cache.ICacheService;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.MapParser;

/** @author {@linkplain http://konradhoeffner.de} */
public class CubeQaQueryParser extends AbstractQueryParser {

	//	private static Logger logger = Logger.getLogger(CubeQaQueryParser.class);
	//	
	//	// Component params
	public final static String END_POINT_PARAM = "END_POINT";
	public final static String DEFAULT_GRAPHS_PARAM = "DEFAULT_GRAPH";
	//	
	public final static String CACHE_CONTEXT = "cubeqa";

	public CubeQaQueryParser(Map<String, Object> params)
	{
		super(params);
	}

	Algorithm algorithm = null; 

	@Override
	public boolean canProcess(IParamMap token) {
		String q = (String) token.getParam(Properties.Literal.TEXT);
		return q != null;
	}
	//
	//	@SuppressWarnings("unchecked")
	@Override
	public List<? extends IResultMap> process(IParamMap paramMap, ServiceProvider serviceProvider, IContext context) throws Exception
	{
		ICacheService cacheService = serviceProvider.get(ICacheService.class);
		String question = (String) paramMap.getParam(Properties.Literal.TEXT);
		String sparqlQuery = cacheService.get(CACHE_CONTEXT, question, String.class);
		// cache miss
		if(sparqlQuery == null)
		{
			List<String> uris = CubeIndex.INSTANCE.getCubeUris(question);
			if(uris.isEmpty()) {return Collections.emptyList();}
			String cubeName = Cube.linkedSpendingCubeName(uris.get(0));
			sparqlQuery = algorithm.template(cubeName, question).sparqlQuery();
			cacheService.put(CACHE_CONTEXT, question, sparqlQuery);
		}

		List<IResultMap> results = new ArrayList<IResultMap>();

		ResultMap r = new ResultMap();
		r.setParam(Properties.SPARQL, sparqlQuery);
		results.add(r);
		return results;
	}

	@Override
	public void setProperties(Map<String, Object> params)
	{
		//			String endPoint = (String) params.get(END_POINT_PARAM);
		//			TODO: get properties from config
		// TODO: super.setProperties first or last or doesn't matter? ask edgard
		super.setProperties(params); // saving parameters into the Interpreter
		// args4j is made for command line options (-this -that) but we can transform our map in such a string so we don't have to assign each parameter by hand
		MapParser parser = new MapParser(new CmdLineParser(Config.INSTANCE));
		parser.parse(params);
	}

	@Override public void startup()
	{
		algorithm = new Algorithm();
	}
	//	
	//	@Override
	//	public void shutdown() {
	//	}

	@Override
	public String getVersion() {
		return "0.9";
	}

}