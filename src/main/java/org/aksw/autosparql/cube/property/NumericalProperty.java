//package org.aksw.autosparql.cube.property;
//
//import org.aksw.autosparql.cube.Cube;
//import org.aksw.autosparql.cube.CubeSparql;
//import com.google.common.collect.HashMultiset;
//import com.google.common.collect.Multiset;
//import com.hp.hpl.jena.query.ResultSet;
//import de.konradhoeffner.commons.IteratorStream;
//
///** for numerical properties with ranges such as xsd decimal or xsd float */
//public class NumericalProperty extends ComponentProperty
//{
//	Multiset<Double> values = HashMultiset.create();
//
//	public NumericalProperty(Cube cube, String uri)
//	{
//		super(cube, uri);
//	}
//
//	void loadValues()
//	{
//		String query = "select ?value (count(?value) as ?cnt)"
//				+ "{?obs a qb:Observation. ?obs <"+uri+"> ?value. } group by ?value";
//		ResultSet rs = cube.sparql.select(query);
//
//		IteratorStream.stream(rs).forEach(qs->values.add(qs.get("value").asLiteral().getDouble(), qs.get("cnt").asLiteral().getInt()));
////		List<Resource> intDataTypes = Arrays.asList(XSD.integer,XSD.positiveInteger,XSD.nonNegativeInteger);
//
////		Optional<Resource> intType = range.flatMap(r->intDataTypes.stream().filter(t->t.getURI().equals(r)).findAny());
////		if(intType.isPresent())
////		{
////			IteratorStream.stream(rs).forEach(qs->values.add(qs.get("value").asLiteral().getInt(), qs.get("cnt").asLiteral().getInt()));
////		}
////		else
////		{
////			IteratorStream.stream(rs).forEach(qs->values.add(qs.get("value").asLiteral().getDouble(), qs.get("cnt").asLiteral().getInt()));
////		}
//	}
//
//}