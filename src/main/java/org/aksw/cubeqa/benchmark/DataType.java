package org.aksw.cubeqa.benchmark;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.XSD;

enum DataType
{
	NUMBER,STRING,URI,BOOLEAN,DATE,YEAR,DATETIME;

	static final Set<String> xsdNumeric = Arrays.asList(XSD.decimal,XSD.xbyte,XSD.xshort,XSD.xint,XSD.xlong,XSD.xfloat,
			XSD.xdouble,XSD.integer,XSD.positiveInteger,XSD.negativeInteger,XSD.nonNegativeInteger,XSD.nonPositiveInteger)
			.stream().map(Resource::getURI).collect(Collectors.toSet());

	public static DataType typeOf(RDFNode node)
	{
		if(node.isResource()) {return URI;}
		String typeUri = node.asLiteral().getDatatypeURI();
		if(typeUri==null||typeUri.equals(XSD.xstring.getURI())) {return STRING;}
		if(xsdNumeric.contains(typeUri)) {return NUMBER;}
		if(typeUri.equals(XSD.xboolean.getURI())) {return BOOLEAN;}
		if(typeUri.equals(XSD.date.getURI())) {return DATE;}
		if(typeUri.equals(XSD.gYear.getURI())) {return YEAR;}
		if(typeUri.equals(XSD.dateTime.getURI())) {return DATETIME;}
		throw new IllegalArgumentException("unknown type :"+typeUri);
//		if(range.equals(XSD.gYear.getURI())) {return TemporalScorer.yearScorer(this);}
//		if(range.equals(XSD.date.getURI())||range.equals(XSD.dateTime.getURI())) {return TemporalScorer.dateScorer(this);}
	}
};