package org.aksw.autosparql.cube;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import org.aksw.linkedspending.tools.DataModel;
import com.clarkparsia.sparqlowl.parser.antlr.SparqlOwlParser.defaultGraphClause_return;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.sparql.engine.http.QueryEngineHTTP;
import com.hp.hpl.jena.vocabulary.DCTerms;

public class CubeSparql implements Serializable
{
	private static final long	serialVersionUID	= 1L;

	static public final CubeSparql LINKED_SPENDING = new CubeSparql(
			"http://linkedspending.aksw.org/instance/",
			"http://linkedspending.aksw.org/ontology/",
			"http://linkedspending.aksw.org/",
			"http://linkedspending.aksw.org/sparql"
//			"http://localhost:8890/sparql"
			);

	public final String prefixInstance;
	public final String prefixOntology;
	public final String superGraph;
	private final String	endpoint;
	private final String prefixes;
	private Set<String> defaultGraphs = new HashSet<>();

	static public CubeSparql linkedSpending(String cubeName)
	{
		CubeSparql cs = new CubeSparql("http://linkedspending.aksw.org/instance/",
				"http://linkedspending.aksw.org/ontology/",
				"http://linkedspending.aksw.org/",
				"http://linkedspending.aksw.org/sparql");
		cs.defaultGraphs.add("http://linkedspending.aksw.org/ontology/");
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

	public ResultSet select(String query)
	{
		QueryEngineHTTP qe = new QueryEngineHTTP(endpoint, prefixes+query);
		defaultGraphs.forEach(qe::addDefaultGraph);
		return qe.execSelect();
	}

	public static String suffix(String uri)
	{
		return uri.substring(Math.max(uri.lastIndexOf('/'),uri.lastIndexOf('#'))+1);
	}
}