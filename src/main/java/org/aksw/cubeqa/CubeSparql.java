package org.aksw.cubeqa;

import java.io.ByteArrayOutputStream;
import org.aksw.cubeqa.rdf.DataCube;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFactory;
import org.apache.jena.query.ResultSetFormatter;
import org.apache.jena.query.ResultSetRewindable;
import org.apache.jena.vocabulary.DCTerms;
import org.apache.jena.vocabulary.RDFS;
import org.apache.jena.vocabulary.XSD;

/** Interface to SPARQL. */
public abstract class CubeSparql
{
	static private CubeSparql finlandAid = null;

	public static synchronized CubeSparql finlandAid()
	{
		if (finlandAid == null)
		{
			finlandAid = getLinkedSpendingInstanceForName("finland-aid");
		}
		return finlandAid;
	}

	public final String cubeUri;
	public final String prefixInstance;
	public final String prefixOntology;
	public final String superGraph;
	final String prefixes;

	static final String PREFIX_LSO = "http://linkedspending.aksw.org/ontology/";
	static final String PREFIX_LS = "http://linkedspending.aksw.org/instance/";

	public static final CubeSparql getLinkedSpendingInstanceForName(String cubeName)
	{
		return getLinkedSpendingInstanceForUri(Cube.linkedSpendingUri(cubeName));
	}

	public static CubeSparql getLinkedSpendingInstanceForUri(String cubeUri)
	{
		//return CubeSparqlEndpoint.getLinkedSpendingInstanceForUri(cubeUri);
		return CubeSparqlHdt.getLinkedSpendingInstanceForUri(cubeUri);
	}

	public CubeSparql(String cubeUri, String prefixInstance, String prefixOntology, String superGraph)
	{
		this.cubeUri = cubeUri;
		this.prefixInstance = prefixInstance;
		this.prefixOntology = prefixOntology;
		this.superGraph = superGraph;
		this.prefixes = "prefix dcterms: <" + DCTerms.getURI() + ">\n"
		// +">\n prefix lso: <"+prefixOntology
				+ "prefix : <" + prefixInstance + ">\n" + "prefix ls: <"+PREFIX_LS+">\n" + "prefix qb: <" + DataCube.BASE + ">\n" + "prefix xsd: <" + XSD.NS + ">\n"
				+ "prefix rdfs: <" + RDFS.uri + ">\n";
	}

	final String cubeUrl(String datasetName)
	{
		return prefixInstance + datasetName;
	}

	public abstract boolean ask(String query);

	public abstract ResultSetRewindable select(String query);

	public static String suffix(String uri)
	{
		return uri.substring(Math.max(uri.lastIndexOf('/'), uri.lastIndexOf('#')) + 1);
	}

	public static String jsonQueryResults(ResultSet rs)
	{
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		ResultSetFormatter.outputAsJSON(outputStream, ResultSetFactory.copyResults(rs));
		return new String(outputStream.toByteArray());
	}
}