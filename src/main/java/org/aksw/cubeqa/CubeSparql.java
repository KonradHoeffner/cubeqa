package org.aksw.cubeqa;

import java.util.ArrayList;
import java.util.List;
import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.vocabulary.DCTerms;
import org.apache.jena.vocabulary.RDFS;
import org.apache.jena.vocabulary.XSD;
import org.rdfhdt.hdt.hdt.HDT;
import org.rdfhdt.hdt.hdt.HDTManager;
import org.rdfhdt.hdtjena.HDTGraph;
import de.konradhoeffner.commons.StopWatch;
import lombok.SneakyThrows;

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
	private final String prefixes;
	private List<String> defaultGraphs = new ArrayList<>();

	private static Model qbench2;

	@SneakyThrows
	private synchronized Model qbench2()
	{
		if(qbench2==null)
		{
			HDT hdt = HDTManager.mapIndexedHDT("qbench2.hdt", null);
			HDTGraph graph = new HDTGraph(hdt);
			qbench2 = ModelFactory.createModelForGraph(graph);
		}
		return qbench2;
	}


	static public CubeSparql getLinkedSpendingInstanceForName(String cubeName)
	{
		return getLinkedSpendingInstanceForUri(Cube.linkedSpendingUri(cubeName));
	}

	static public CubeSparql getLinkedSpendingInstanceForUri(String cubeUri)
	{
		CubeSparql cs = new CubeSparql(cubeUri,
				"http://linkedspending.aksw.org/instance/",
				"http://linkedspending.aksw.org/ontology/",
				"http://linkedspending.aksw.org/");
				//"http://cubeqa.aksw.org/sparql"
		cs.defaultGraphs.add("http://linkedspending.aksw.org/ontology/");
		cs.defaultGraphs.add("http://linkedspending.aksw.org/"+cubeUri.substring(cubeUri.lastIndexOf('/')+1));
		return cs;
	}

	public CubeSparql(String cubeUri, String prefixInstance, String prefixOntology, String superGraph)
	{
		this.cubeUri=cubeUri;
		this.prefixInstance = prefixInstance;
		this.prefixOntology = prefixOntology;
		this.superGraph = superGraph;
		this.prefixes = "prefix dcterms: <"+DCTerms.getURI()+">\n"
				//					+">\n prefix lso: <"+prefixOntology
				+"prefix : <"+prefixInstance+">\n"
				+"prefix ls: <http://linkedspending.aksw.org/instance/>\n"
				+"prefix qb: <"+DataCube.BASE+">\n"
				+"prefix xsd: <"+XSD.NS+">\n"
				+"prefix rdfs: <"+RDFS.uri+">\n";
	}

	String cubeUrl(String datasetName) {return prefixInstance+datasetName;}

	public boolean ask(String query)
	{
		StopWatch watch = StopWatches.INSTANCE.getWatch("sparql");
		watch.start();
		
		try(QueryExecution qe = QueryExecutionFactory.create(query,qbench2()))
		{
			return qe.execAsk();
		} catch(Exception e) {throw new RuntimeException("Error on SPARQL ASK with query:\n"+query,e);}
		finally {watch.stop();}
	}

	public ResultSetRewindable select(String query)
	{
		query = prefixes+query;
		StopWatch watch = StopWatches.INSTANCE.getWatch("sparql");
		watch.start();
		try(QueryExecution qe = QueryExecutionFactory.create(query,qbench2()))
		{		

			return ResultSetFactory.copyResults(qe.execSelect());
		} catch(Exception e) {throw new RuntimeException("Error on SPARQL SELECT on with query:\n"+query,e);}
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