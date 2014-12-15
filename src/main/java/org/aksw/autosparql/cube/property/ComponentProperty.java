package org.aksw.autosparql.cube.property;

import static de.konradhoeffner.commons.IteratorStream.stream;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.aksw.autosparql.cube.Cube;
import org.aksw.autosparql.cube.CubeSparql;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Resource;
import de.konradhoeffner.commons.Pair;

/** Represents a component property of a RDF Data Cube.
 * Implements the Multiton Pattern, with the key being the combination of cube name and uri, because information about values is safed.
 * Immutable except for the labels.*/
public class ComponentProperty
{
	//	enum Domain {TIME,DATE,YEAR,AGE,CURRENCY,OTHER};


	public final Optional<Resource> range;

	public final Cube cube;
	public final String uri;
	//	public final Domain domain;

	public final Set<String> labels;
	public final PropertyType type;

	static private final Map<Pair<String>,ComponentProperty> instances = new HashMap<>();

	//	static Domain propertyDomain(String propertyUri)
	//	{
	//		try
	//		{
	//			return Domain.valueOf(propertyUri.toUpperCase());
	//		}
	//		catch(IllegalArgumentException e) {return Domain.OTHER;}
	//	}

	Optional<Resource> guessRange()
	{
		// todo implement
		return Optional.empty();
	}

	public ComponentProperty(Cube cubeUri, String uri, PropertyType type)
	{
		CubeSparql sparql = CubeSparql.LINKED_SPENDING;
		this.cube = cubeUri;
		this.uri = uri;
		Set<String> labels = new HashSet<>();
		labels.add(CubeSparql.suffix(uri));
		{
			String labelQuery = "select distinct(?l) {<"+uri+"> rdfs:label ?l}";
			labels.addAll(stream(sparql.select(labelQuery))
					.map(qs->qs.get("l").asLiteral().getLexicalForm()).collect(Collectors.toSet()));
		}
		{
			String rangeQuery = "select ?range {<"+uri+"> rdfs:range ?range}";
			ResultSet rs = sparql.select(rangeQuery);
			if(rs.hasNext())
			{
				range = Optional.of(rs.nextSolution().get("range").asResource());
			}
			else {range = guessRange();}
		}

		this.labels=Collections.unmodifiableSet(labels);
		//		String query = "select distinct(?v) {?o a qb:Observation. ?o qb:dataSet <"+uri+">."
		//				+ "?o <"+uri+"> ?v. } limit 1000";
		//		CubeSparql.LINKED_SPENDING.select(query);
		//		this.domain=propertyDomain(propertyUri);
		this.type=type;
	}

	public static synchronized ComponentProperty getInstance(Cube cubeUri, String uri, String type)
	{
		Pair<String> key = new Pair<String>(cubeUri.uri, uri);
		ComponentProperty instance = instances.get(key);
		if(instance==null)
		{
			instance = new ComponentProperty(cubeUri, uri, PropertyType.ofRdfType(type));
		}
		return instance;
	}

	@Override public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((cube == null) ? 0 : cube.hashCode());
		result = prime * result + ((uri == null) ? 0 : uri.hashCode());
		return result;
	}

	@Override public boolean equals(Object obj)
	{
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		ComponentProperty other = (ComponentProperty) obj;
		if (cube == null)
		{
			if (other.cube != null) return false;
		}
		else if (!cube.equals(other.cube)) return false;
		if (uri == null)
		{
			if (other.uri != null) return false;
		}
		else if (!uri.equals(other.uri)) return false;
		return true;
	}

	@Override public String toString()
	{
		return uri;
	}

}