package org.aksw.autosparql.cube;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.ToString;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.RDFNode;

/** Represents an RDF Data Cube with its component properties */
@AllArgsConstructor
@ToString
public class Cube
{
	public final String name;
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
					" ls:black-budget qb:structure ?dsd. ?dsd qb:component ?comp."+
					" {?comp qb:dimension ?p.} UNION {?comp qb:attribute ?p.} UNION {?comp qb:measure ?p.} "+
					" ?p a ?type. FILTER (?type != <http://www.w3.org/1999/02/22-rdf-syntax-ns#Property>)"+
					" OPTIONAL {?p rdfs:label ?label}"+
					"}";

			ResultSet rs = CubeSparql.LINKED_SPENDING.select(query);

			while(rs.hasNext())
			{
				QuerySolution qs = rs.nextSolution();

				// because of ComponentProperty's multiton pattern, having the same property multiple times is not a problem and in fact necessary for multiple labels
				String propertyUri = qs.get("p").asResource().getURI();
				ComponentProperty property = ComponentProperty.getInstance(cubeName, propertyUri, qs.get("type").asResource().getURI());
				properties.put(propertyUri, property);
				if(qs.contains("label")) {property.labels.add(qs.get("label").asLiteral().getLexicalForm());}
			}
			c = new Cube(cubeName, properties);
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