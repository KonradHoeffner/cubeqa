package de.konradhoeffner.commons.rdf;

import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.*;

/** RDF Data Cube vocabulary */
public class DataCube
{
	static public final String				BASE					= "http://purl.org/linked-data/cube#";
	// in addition to myResource.getURI() because switch statements need constants
	static public final String				DIMENSION_PROPERTY_URI = BASE+"DimensionProperty";
	static public final String				MEASURE_PROPERTY_URI = BASE+"MeasureProperty";
	static public final String				ATTRIBUTE_PROPERTY_URI = BASE+"AttributeProperty";

	static public final Resource	DataStructureDefinition	= ResourceFactory
			.createResource(BASE + "DataStructureDefinition");
	static public final Resource	DataSet			= ResourceFactory.createResource(BASE + "DataSet");
	static public final Resource	ComponentProperty		= ResourceFactory.createResource(BASE + "ComponentProperty");
	static public final Resource	DimensionProperty		= ResourceFactory.createResource(BASE + "DimensionProperty");
	static public final Resource	MeasureProperty			= ResourceFactory.createResource(BASE + "MeasureProperty");
	static public final Resource	AttributeProperty		= ResourceFactory.createResource(BASE + "AttributeProperty");
	static public final Resource	SliceKey				= ResourceFactory.createResource(BASE + "SliceKey");
	static public final Resource	HierarchicalCodeList	= ResourceFactory.createResource(BASE + "HierarchicalCodeList");
	static public final Resource	ComponentSpecification	= ResourceFactory.createResource(BASE + "ComponentSpecification");
	static public final Resource	Observation				= ResourceFactory.createResource(BASE + "Observation");
	static public final Resource	Slice			= ResourceFactory.createResource(BASE + "Slice");

	static public final Property	component				= ResourceFactory.createProperty(BASE + "component");
	static public final Property	dataSet					= ResourceFactory.createProperty(BASE + "dataSet");
	static public final Property	structure				= ResourceFactory.createProperty(BASE + "structure");
	static public final Property	componentProperty		= ResourceFactory.createProperty(BASE + "componentProperty");
	static public final Property	dimension				= ResourceFactory.createProperty(BASE + "dimension");
	static public final Property	measure					= ResourceFactory.createProperty(BASE + "measure");
	static public final Property	attribute				= ResourceFactory.createProperty(BASE + "attribute");
	static public final Property	concept					= ResourceFactory.createProperty(BASE + "concept");
	static public final Property	slice					= ResourceFactory.createProperty(BASE + "slice");
	static public final Property	sliceStructure			= ResourceFactory.createProperty(BASE + "sliceStructure");
	static public final Property	parentChildProperty		= ResourceFactory.createProperty(BASE + "parentChildProperty");
}