package org.aksw.cubeqa.property;

import org.aksw.linkedspending.tools.DataModel;

public enum PropertyType
{
	ATTRIBUTE,DIMENSION,MEASURE;

	static public PropertyType ofRdfType(String type)
	{
		switch(type)
		{
			case DataModel.DataCube.ATTRIBUTE_PROPERTY_URI:return ATTRIBUTE;
			case DataModel.DataCube.DIMENSION_PROPERTY_URI:return DIMENSION;
			case DataModel.DataCube.MEASURE_PROPERTY_URI:return MEASURE;
			default:throw new IllegalArgumentException(type+" is not a subtype of qb:ComponentProperty");
		}
	}
}