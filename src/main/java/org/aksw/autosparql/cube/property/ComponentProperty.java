package org.aksw.autosparql.cube.property;

import static de.konradhoeffner.commons.IteratorStream.stream;
import static org.aksw.linkedspending.tools.DataModel.*;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.aksw.autosparql.cube.Cube;
import org.aksw.autosparql.cube.property.scorer.*;
import org.aksw.autosparql.cube.CubeSparql;
import org.aksw.autosparql.cube.property.scorer.DateScorer;
import org.aksw.autosparql.cube.property.scorer.ObjectPropertyScorer;
import org.aksw.autosparql.cube.property.scorer.Scorer;
import org.aksw.autosparql.cube.property.scorer.StringScorer;
import org.aksw.linkedspending.tools.DataModel;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.XSD;
import de.konradhoeffner.commons.Pair;

/** Represents a component property of a RDF Data Cube.
 * Implements the Multiton Pattern, with the key being the combination of cube name and uri, because information about values is safed.
 * Immutable except for the labels.*/
public class ComponentProperty implements Serializable
{
	private static final long	serialVersionUID	= 1L;

	public final String range;

	public final Cube cube;
	public final String uri;
	//	public final Domain domain;

	public final Set<String> labels;
	//	public final PropertyType type;

	public final Scorer scorer;

	static private final Map<Pair<String>,ComponentProperty> instances = new HashMap<>();

	//	static Domain propertyDomain(String propertyUri)
	//	{
	//		try
	//		{
	//			return Domain.valueOf(propertyUri.toUpperCase());
	//		}
	//		catch(IllegalArgumentException e) {return Domain.OTHER;}
	//	}

	String guessRange()
	{
		// todo implement
		return null;
	}

	public ComponentProperty(Cube cube, String uri)//, PropertyType type)
	{
		this.cube = cube;
		this.uri = uri;
		Set<String> labels = new HashSet<>();
		{
			labels.add(CubeSparql.suffix(uri));
			labels.addAll(stream(cube.sparql.select("select distinct(?l) {<"+uri+"> rdfs:label ?l}"))
					.map(qs->qs.get("l").asLiteral().getLexicalForm()).collect(Collectors.toSet()));
		}
		Scorer scorer = null;
		Set<String> types = new HashSet<>();
		{
			String typeQuery = "select distinct(?t) {<"+uri+"> a ?t."
					+ "FILTER (?type != <"+RDF.Property.getURI()+"> && ?type != <"+DataModel.DataCube.ComponentProperty.getURI()+">)}";
			types.addAll(stream(cube.sparql.select(typeQuery))
					.map(qs->qs.get("t").asResource().getURI()).collect(Collectors.toSet()));
			// easiest and fastest way to determine type of values, but isn't always modelled
			String rangeQuery = "select ?range {<"+uri+"> rdfs:range ?range}";
			ResultSet rs = cube.sparql.select(rangeQuery);
			if(rs.hasNext())
			{
				range = rs.nextSolution().get("range").asResource().getURI();
			}
			else {range = guessRange();}
			scorer = scorer(types);
		}

		this.labels=Collections.unmodifiableSet(labels);
		//		String query = "select distinct(?v) {?o a qb:Observation. ?o qb:dataSet <"+uri+">."
		//				+ "?o <"+uri+"> ?v. } limit 1000";
		//		CubeSparql.LINKED_SPENDING.select(query);
		//		this.domain=propertyDomain(propertyUri);
		//		this.type=type;
		this.scorer=scorer;
	}

	private Scorer scorer(Set<String> types)
	{
		for(String type: types)
		{
			switch(type)
			{
				case Owl.OBJECT_PROPERTY_URI:return new ObjectPropertyScorer(this);
				case Owl.DATATYPE_PROPERTY_URI:
				default:
			}
		}

		if(range!=null)
		{
			if(range.startsWith(XSD.getURI()))
			{
				Set<String> xsdNumeric = Arrays.asList(XSD.decimal,XSD.xbyte,XSD.xshort,XSD.xint,XSD.xlong,XSD.xfloat,XSD.xdouble)
						.stream().map(Resource::getURI).collect(Collectors.toSet());
				if(range.endsWith("Integer")||xsdNumeric.contains(uri)) {return new NumericScorer(this);}
				if(range.equals(XSD.xstring.getURI())) {return new StringScorer(this);}
				//					if(r.equals(XSD.xboolean.getURI())) {return new BooleanScorer(this);} // TODO investigate do we need a boolean scorer?
				if(range.equals(XSD.date.getURI())||range.equals(XSD.dateTime.getURI())) {return new DateScorer(this);}
			};
		}



		return scorer;
	}

	public static synchronized ComponentProperty getInstance(Cube cubeUri, String uri)//, String type)
	{
		Pair<String> key = new Pair<String>(cubeUri.uri, uri);
		ComponentProperty instance = instances.get(key);
		if(instance==null)
		{
			instance = new ComponentProperty(cubeUri, uri);//, PropertyType.ofRdfType(type));
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