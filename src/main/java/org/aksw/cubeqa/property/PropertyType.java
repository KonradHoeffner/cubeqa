package org.aksw.cubeqa.property;

import org.aksw.cubeqa.rdf.DataCube;

public enum PropertyType
{
	ATTRIBUTE,DIMENSION,MEASURE;

	static public PropertyType ofRdfType(String type)
	{
		switch(type)
		{
			case DataCube.ATTRIBUTE_PROPERTY_URI:return ATTRIBUTE;
			case DataCube.DIMENSION_PROPERTY_URI:return DIMENSION;
			case DataCube.MEASURE_PROPERTY_URI:return MEASURE;
			default:throw new IllegalArgumentException(type+" is not a subtype of qb:ComponentProperty");
		}
	}
}