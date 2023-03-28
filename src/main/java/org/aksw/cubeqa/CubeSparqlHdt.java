package org.aksw.cubeqa;

import java.io.Serializable;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.ResultSetFactory;
import org.apache.jena.query.ResultSetRewindable;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.rdfhdt.hdt.hdt.HDT;
import org.rdfhdt.hdt.hdt.HDTManager;
import org.rdfhdt.hdtjena.HDTGraph;
import de.konradhoeffner.commons.StopWatch;
import lombok.SneakyThrows;

/** Interface to SPARQL. */
public class CubeSparqlHdt extends CubeSparql implements Serializable
{
	private static final long serialVersionUID = 1L;

	private final Model model;

	static public CubeSparql getLinkedSpendingInstanceForUri(String cubeUri)
	{
		CubeSparql cs = new CubeSparqlHdt(cubeUri, PREFIX_LS, PREFIX_LSO, "http://linkedspending.aksw.org/", "qbench2.hdt");
		return cs;
	}

	@SneakyThrows
	public CubeSparqlHdt(String cubeUri, String prefixInstance, String prefixOntology, String superGraph, String hdtFileName)
	{
		super(cubeUri, prefixInstance, prefixOntology, superGraph);
		HDT hdt = HDTManager.mapIndexedHDT("qbench2.hdt", null);
		HDTGraph graph = new HDTGraph(hdt);
		model = ModelFactory.createModelForGraph(graph);
	}

	public boolean ask(String query)
	{
		StopWatch watch = StopWatches.INSTANCE.getWatch("hdt");
		watch.start();

		try (QueryExecution qe = QueryExecutionFactory.create(query, model))
		{
			return qe.execAsk();
		} catch (Exception e)
		{
			throw new RuntimeException("Error on SPARQL ASK with query:\n" + query, e);
		} finally
		{
			watch.stop();
		}
	}

	public ResultSetRewindable select(String query)
	{
		query = prefixes + query;
		StopWatch watch = StopWatches.INSTANCE.getWatch("hdt");
		watch.start();
		try (QueryExecution qe = QueryExecutionFactory.create(query, model))
		{
			return ResultSetFactory.copyResults(qe.execSelect());
		} catch (Exception e)
		{
			throw new RuntimeException("Error on SPARQL SELECT on with query:\n" + query, e);
		} finally
		{
			watch.stop();
		}
	}
	
}