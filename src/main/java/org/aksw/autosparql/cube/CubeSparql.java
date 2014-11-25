package org.aksw.autosparql.cube;

import org.aksw.linkedspending.tools.DataModel;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.sparql.engine.http.QueryEngineHTTP;
import com.hp.hpl.jena.vocabulary.DCTerms;

public class CubeSparql
{
	static public final String prefixInstance="http://linkedspending.aksw.org/instance/";
	static public final String prefixOntology="http://linkedspending.aksw.org/ontology/";
	static public final String SUPER_GRAPH = "http://linkedspending.aksw.org/";
	private static final String	ENDPOINT	= "http://linkedspending.aksw.org/sparql";

	public static final String PREFIXES = "prefix dcterms: <"+DCTerms.getURI()
			+">\n prefix : <"+prefixInstance
//			+">\n prefix lso: <"+prefixOntology
			+">\n prefix qb: <"+DataModel.DataCube.base+">\n";

	static String cubeUrl(String datasetName) {return prefixInstance+datasetName;}

	public static ResultSet selectPrefixed(String query)
	{
		return select(PREFIXES+query);
	}

	public static ResultSet select(String query)
	{
		QueryEngineHTTP qe = new QueryEngineHTTP(ENDPOINT, query);
		return qe.execSelect();
	}

	public static String suffix(String uri)
	{
		return uri.substring(Math.max(uri.lastIndexOf('/'),uri.lastIndexOf('#'))+1);
	}
}