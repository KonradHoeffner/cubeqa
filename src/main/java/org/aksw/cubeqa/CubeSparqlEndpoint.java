package org.aksw.cubeqa;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.ResultSetFactory;
import org.apache.jena.query.ResultSetRewindable;
import org.apache.jena.sparql.exec.http.QueryExecutionHTTP;
import org.apache.jena.sparql.exec.http.QueryExecutionHTTPBuilder;

import de.konradhoeffner.commons.StopWatch;

/** Interface to SPARQL. */
public class CubeSparqlEndpoint extends CubeSparql implements Serializable
{
	private static final long serialVersionUID = 1L;

	private final String endpoint;
	private List<String> defaultGraphs = new ArrayList<>();
	// "http://localhost:8890/sparql");
	// "http://linkedspending.aksw.org/sparql");
	//"http://cubeqa.aksw.org/sparql");
	static final String ENDPOINT_LS = "http://localhost:8890/sparql";
	static final String GRAPH_LS = "http://linkedspending.aksw.org";

	public static CubeSparqlEndpoint getLinkedSpendingInstanceForUri(String cubeUri)
	{
		CubeSparqlEndpoint cs = new CubeSparqlEndpoint(cubeUri, PREFIX_LS, PREFIX_LSO, GRAPH_LS, ENDPOINT_LS);
		cs.defaultGraphs.add(GRAPH_LS);
		//cs.defaultGraphs.add(LSO);
		//cs.defaultGraphs.add(LS + cubeUri.substring(cubeUri.lastIndexOf('/') + 1));
		return cs;
	}

	public CubeSparqlEndpoint(String cubeUri, String prefixInstance, String prefixOntology, String superGraph, String endpoint)
	{
		super(cubeUri, prefixInstance, prefixOntology, superGraph);
		this.endpoint = endpoint;
	}

	public boolean ask(String query)
	{
		StopWatch watch = StopWatches.INSTANCE.getWatch("sparql");
		watch.start();
		QueryExecutionHTTPBuilder builder = QueryExecutionHTTP.create().endpoint(endpoint).query(prefixes + query);
		defaultGraphs.forEach(builder::addDefaultGraphURI);
		try (QueryExecution qe = builder.build())
		{
			return qe.execAsk();
		} catch (Exception e)
		{
			throw new RuntimeException("Error on SPARQL ASK on endpoint " + endpoint + " with query:\n" + query, e);
		} finally
		{
			watch.stop();
		}
	}

	public ResultSetRewindable select(String query)
	{
		query = prefixes + query;
		StopWatch watch = StopWatches.INSTANCE.getWatch("sparql");
		watch.start();
		QueryExecutionHTTPBuilder builder = QueryExecutionHTTP.create().endpoint(endpoint);
		defaultGraphs.forEach(builder::addDefaultGraphURI);
		try (QueryExecution qe = builder.query(query).build())
		{

			return ResultSetFactory.copyResults(qe.execSelect());
		} catch (Exception e)
		{
			throw new RuntimeException("Error on SPARQL SELECT on endpoint " + endpoint + " with query:\n" + query, e);
		} finally
		{
			watch.stop();
		}
	}
}