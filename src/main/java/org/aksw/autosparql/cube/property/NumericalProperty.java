package org.aksw.autosparql.cube.property;

import org.aksw.autosparql.cube.Cube;
import org.aksw.autosparql.cube.CubeSparql;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import com.hp.hpl.jena.query.ResultSet;
import de.konradhoeffner.commons.IteratorStream;

/** for numerical properties with ranges such as xsd decimal or xsd float */
public class NumericalProperty extends ComponentProperty
{
	Multiset<Double> values = HashMultiset.create();

	public NumericalProperty(Cube cube, String uri, PropertyType type)
	{
		super(cube, uri, type);
	}

	void loadValues()
	{
		String query = "select ?value (count(?value) as ?cnt)"
				+ "{?obs a qb:Observation. ?obs <"+uri+"> ?value. } group by ?value";
		ResultSet rs = CubeSparql.linkedSpending(cube.name).select(query);

		IteratorStream.stream(rs).forEach(qs->values.add(qs.get("value").asLiteral().getDouble(), qs.get("cnt").asLiteral().getInt()));
//		List<Resource> intDataTypes = Arrays.asList(XSD.integer,XSD.positiveInteger,XSD.nonNegativeInteger);

//		Optional<Resource> intType = range.flatMap(r->intDataTypes.stream().filter(t->t.getURI().equals(r)).findAny());
//		if(intType.isPresent())
//		{
//			IteratorStream.stream(rs).forEach(qs->values.add(qs.get("value").asLiteral().getInt(), qs.get("cnt").asLiteral().getInt()));
//		}
//		else
//		{
//			IteratorStream.stream(rs).forEach(qs->values.add(qs.get("value").asLiteral().getDouble(), qs.get("cnt").asLiteral().getInt()));
//		}
	}

	double valueScore(double value)
	{
		if(values.contains(value)) {return 1;}
		double maxSimilarity = 0;
		for(double v: values) {maxSimilarity = Math.max(maxSimilarity, similarity(v,value));}
		return maxSimilarity;
	}

	private double similarity(double v, double value)
	{
		// TODO steeper falloff
		// TODO incorporate number of occurrences
		double eps = 0.01;
		if(Math.abs(v-value)<eps) return 1;
		if(v==0&&value==0) return 1;
		if(v==0^value==0) return 0;
		return Math.min(Math.abs(v/value),Math.abs(value/v));
	}



}