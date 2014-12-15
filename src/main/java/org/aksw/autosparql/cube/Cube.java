package org.aksw.autosparql.cube;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.aksw.autosparql.cube.property.ComponentProperty;
import org.aksw.linkedspending.tools.DataModel;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.vocabulary.RDF;

/** Represents an RDF Data Cube with its component properties */
@RequiredArgsConstructor
@ToString
public class Cube
{
	public final String name;
	public final String uri;
	//	public final Set<String> labels = new TreeSet<String>();
	public final Map<String,ComponentProperty> properties;

	static Map<String,Cube> instances = new HashMap<>();

	static String extractName(RDFNode node)
	{
		String uri = node.asResource().getURI();
		return uri.substring(uri.lastIndexOf("/")+1);
	}

	public static synchronized Cube getInstance(String cubeName)
	{
		Cube c = instances.get(cubeName);
		if(c==null)
		{
			Map<String,ComponentProperty> properties = new HashMap<>();

			String query = "select distinct ?p ?type ?label "+
					"from <http://linkedspending.aksw.org/"+cubeName+"> "+
					"from <http://linkedspending.aksw.org/ontology/> "+
					"{"+
					" ls:"+cubeName+" qb:structure ?dsd. ?dsd qb:component ?comp."+
					" {?comp qb:dimension ?p.} UNION {?comp qb:attribute ?p.} UNION {?comp qb:measure ?p.} "+
					" ?p a ?type. FILTER (?type != <"+RDF.Property.getURI()+"> && ?type != <"+DataModel.DataCube.ComponentProperty.getURI()+">)"+
//					" OPTIONAL {?p rdfs:label ?label}"+
					"}";

			ResultSet rs = CubeSparql.LINKED_SPENDING.select(query);
			String uri = "http://linkedspending.aksw.org/instance/"+cubeName;
			c = new Cube(cubeName,uri, properties);
			while(rs.hasNext())
			{
				QuerySolution qs = rs.nextSolution();

				// because of ComponentProperty's multiton pattern, having the same property multiple times is not a problem and in fact necessary for multiple labels
				String propertyUri = qs.get("p").asResource().getURI();
				ComponentProperty property = ComponentProperty.getInstance(c, propertyUri, qs.get("type").asResource().getURI());
				properties.put(propertyUri, property);
//				if(qs.contains("label")) {property.labels.add(qs.get("label").asLiteral().getLexicalForm());}
			}
		}
		return c;
	}

//	static Set<Cube> fromEndpoint(String endpointUrl, String cubeName)
//	{
//		Set<Cube> cubes = new HashSet<>();
//		String query = "select ?qb ?id {?qb a qb:DataSet. }";
//		return cubes;
//	}

	@Override public int hashCode() {return name.hashCode();}

	@Override public boolean equals(Object obj)
	{
		if(!(obj instanceof Cube)) return false;
		return this.name.equals(((Cube)obj).name);
	}

}