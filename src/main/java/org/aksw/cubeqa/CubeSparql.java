package org.aksw.cubeqa;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.aksw.linkedspending.tools.DataModel;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.sparql.engine.http.QueryEngineHTTP;
import com.hp.hpl.jena.vocabulary.DCTerms;

/** Interface to SPARQL. */
public class CubeSparql implements Serializable
{
	private static final long	serialVersionUID	= 1L;

//	static public final CubeSparql LINKED_SPENDING = new CubeSparql(
//			"http://linkedspending.aksw.org/instance/",
//			"http://linkedspending.aksw.org/ontology/",
//			"http://linkedspending.aksw.org/",
//			"http://linkedspending.aksw.org/sparql"
////			"http://localhost:8890/sparql"
//			);

	static public final CubeSparql FINLAND_AID = linkedSpending("finland-aid");

	public final String prefixInstance;
	public final String prefixOntology;
	public final String superGraph;
	private final String	endpoint;
	private final String prefixes;
	private List<String> defaultGraphs = new ArrayList<>();

	static public CubeSparql linkedSpending(String cubeName)
	{
		CubeSparql cs = new CubeSparql("http://linkedspending.aksw.org/instance/",
				"http://linkedspending.aksw.org/ontology/",
				"http://linkedspending.aksw.org/",
				"http://localhost:8890/sparql");
//				"http://linkedspending.aksw.org/sparql");
		cs.defaultGraphs.add("http://linkedspending.aksw.org/ontology");
		cs.defaultGraphs.add("http://linkedspending.aksw.org/"+cubeName);
		return cs;
	}

	public CubeSparql(String prefixInstance, String prefixOntology, String superGraph, String endpoint)
	{
		super();
		this.prefixInstance = prefixInstance;
		this.prefixOntology = prefixOntology;
		this.superGraph = superGraph;
		this.endpoint = endpoint;
		 prefixes = "prefix dcterms: <"+DCTerms.getURI()
					+">\n prefix : <"+prefixInstance
//					+">\n prefix lso: <"+prefixOntology
					+">\n prefix qb: <"+DataModel.DataCube.BASE+">\n";
	}

	String cubeUrl(String datasetName) {return prefixInstance+datasetName;}

	public boolean ask(String query)
	{
		try
		{
		QueryEngineHTTP qe = new QueryEngineHTTP(endpoint, prefixes+query);
		defaultGraphs.forEach(qe::addDefaultGraph);

		return qe.execAsk();
		} catch(Exception e) {throw new RuntimeException("Error on SPARQL ASK on endpoint "+endpoint+" with query:\n"+query,e);}
	}

	public ResultSet select(String query)
	{
		try
		{
		QueryEngineHTTP qe = new QueryEngineHTTP(endpoint, prefixes+query);
		qe.setDefaultGraphURIs(defaultGraphs);

		return qe.execSelect();
		} catch(Exception e) {throw new RuntimeException("Error on sparql select on endpoint "+endpoint+" with query:\n"+query,e);}
	}

	public static String suffix(String uri)
	{
		return uri.substring(Math.max(uri.lastIndexOf('/'),uri.lastIndexOf('#'))+1);
	}
}