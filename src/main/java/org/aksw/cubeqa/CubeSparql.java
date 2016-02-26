package org.aksw.cubeqa;

import java.util.ArrayList;
import java.util.List;
import java.io.Serializable;
import org.aksw.commons.util.StopWatch;
import com.hp.hpl.jena.query.*;
import com.hp.hpl.jena.sparql.engine.http.QueryEngineHTTP;
import com.hp.hpl.jena.vocabulary.DCTerms;
import de.konradhoeffner.commons.rdf.DataCube;

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
			return ResultSetFactory.copyResults(qe.execSelect());
		} catch(Exception e) {throw new RuntimeException("Error on sparql select on endpoint "+endpoint+" with query:\n"+query,e);}
		finally {watch.stop();}
	}

	public static String suffix(String uri)
	{
		return uri.substring(Math.max(uri.lastIndexOf('/'),uri.lastIndexOf('#'))+1);
	}
}