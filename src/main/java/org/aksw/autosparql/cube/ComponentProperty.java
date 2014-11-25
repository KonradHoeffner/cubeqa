package org.aksw.autosparql.cube;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import de.konradhoeffner.commons.Pair;

/** Represents a component property of a RDF Data Cube.
 * Implements the Multiton Pattern, with the key being the combination of cube name and uri, because information about values is safed.
 * Immutable except for the labels.*/
public class ComponentProperty
{
	enum Domain {TIME,DATE,YEAR,AGE,CURRENCY,OTHER};

	public final String cubeName;
	public final String uri;
	//	public final Domain domain;

	public final Set<String> labels = new HashSet<String>();

	static private final Map<Pair<String>,ComponentProperty> instances = new HashMap<>();

	static Domain propertyDomain(String propertyUri)
	{
		try
		{
			return Domain.valueOf(propertyUri.toUpperCase());
		}
		catch(IllegalArgumentException e) {return Domain.OTHER;}
	}

	public ComponentProperty(String cubeName, String uri, String type)
	{
		this.cubeName = cubeName;
		this.uri = uri;
		labels.add(CubeSparql.suffix(uri));
		//		this.domain=propertyDomain(propertyUri);
	}

	static synchronized ComponentProperty getInstance(String cubeName, String uri, String type)
	{
		Pair<String> key = new Pair<String>(cubeName, uri);
		ComponentProperty instance = instances.get(key);
		if(instance==null)
		{
			instance = new ComponentProperty(cubeName, uri, type);
		}
		return instance;
	}

	@Override public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((cubeName == null) ? 0 : cubeName.hashCode());
		result = prime * result + ((uri == null) ? 0 : uri.hashCode());
		return result;
	}

	@Override public boolean equals(Object obj)
	{
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		ComponentProperty other = (ComponentProperty) obj;
		if (cubeName == null)
		{
			if (other.cubeName != null) return false;
		}
		else if (!cubeName.equals(other.cubeName)) return false;
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