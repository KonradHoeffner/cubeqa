package org.aksw.autosparql.cube;

import lombok.Data;

public class Measure extends ComponentProperty
{

	public Measure(String cubeName, String name,String propertyUri)
	{
		super(cubeName, name,propertyUri);
	}

}
