package org.aksw.cubeqa;

import java.util.ArrayList;
import java.util.List;
import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import org.apache.jena.query.*;
import org.apache.jena.sparql.engine.http.QueryEngineHTTP;
import org.apache.jena.vocabulary.DCTerms;
import de.konradhoeffner.commons.StopWatch;
import org.aksw.cubeqa.rdf.DataCube;

/** Interface to SPARQL. */
public class CubeSparql implements Serializable
{
	private static final long	serialVersionUID	= 1L;

	static private CubeSparql finlandAid = null;
	static public synchronized CubeSparql finlandAid()
	{
		if(finlandAid==null) {finlandAid = getLinkedSpendingInstanceForName("finland-aid");}
		return finlandAid;
	}

	public final String cubeUri;
	public final String prefixInstance;
	public final String prefixOntology;
	public final String superGraph;
	private final String	endpoint;
	private final String prefixes;
	private List<String> defaultGraphs = new ArrayList<>();

	static public CubeSparql getLinkedSpendingInstanceForName(String cubeName)
	{
		return getLinkedSpendingInstanceForUri(Cube.linkedSpendingUri(cubeName));
	}

	static public CubeSparql getLinkedSpendingInstanceForUri(String cubeUri)
	{
		CubeSparql cs = new CubeSparql(cubeUri,
				"http://linkedspending.aksw.org/instance/",
				"http://linkedspending.aksw.org/ontology/",
				"http://linkedspending.aksw.org/",
				// local Virtuoso SPARQL endpoint has a NAN bug
//												"http://localhost:8890/sparql");
//				"http://linkedspending.aksw.org/sparql");
			"http://cubeqa.aksw.org/sparql");
		cs.defaultGraphs.add("http://linkedspending.aksw.org/ontology/");
		cs.defaultGraphs.add("http://linkedspending.aksw.org/"+cubeUri.substring(cubeUri.lastIndexOf('/')+1));
		return cs;
	}

	public CubeSparql(String cubeUri, String prefixInstance, String prefixOntology, String superGraph, String endpoint)
	{
		this.cubeUri=cubeUri;
		this.prefixInstance = prefixInstance;
		this.prefixOntology = prefixOntology;
		this.superGraph = superGraph;
		this.endpoint = endpoint;
		this.prefixes = "prefix dcterms: <"+DCTerms.getURI()
		+">\n prefix : <"+prefixInstance
		//					+">\n prefix lso: <"+prefixOntology
		+">\n prefix qb: <"+DataCube.BASE+">\n";
	}

	String cubeUrl(String datasetName) {return prefixInstance+datasetName;}

	public boolean ask(String query)
	{
		StopWatch watch = StopWatches.INSTANCE.getWatch("sparql");
		watch.start();
		try(QueryEngineHTTP qe = new QueryEngineHTTP(endpoint, prefixes+query);)
		{			
			defaultGraphs.forEach(qe::addDefaultGraph);
			return qe.execAsk();
		} catch(Exception e) {throw new RuntimeException("Error on SPARQL ASK on endpoint "+endpoint+" with query:\n"+query,e);}
		finally {watch.stop();}
	}

	public ResultSetRewindable select(String query)
	{
		StopWatch watch = StopWatches.INSTANCE.getWatch("sparql");
		watch.start();
		try(QueryEngineHTTP qe = new QueryEngineHTTP(endpoint, prefixes+query);) 
		{
			qe.setDefaultGraphURIs(defaultGraphs);
			return ResultSetFactory.copyResults(qe.execSelect());
		} catch(Exception e) {throw new RuntimeException("Error on SPARQL SELECT on endpoint "+endpoint+" with query:\n"+query,e);}
		finally {watch.stop();}
	}

	public static String suffix(String uri)
	{
		return uri.substring(Math.max(uri.lastIndexOf('/'),uri.lastIndexOf('#'))+1);
	}
	
	public static String jsonQueryResults(ResultSet rs)
	{
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		ResultSetFormatter.outputAsJSON(outputStream, ResultSetFactory.copyResults(rs));		
		return new String(outputStream.toByteArray());
	}
}