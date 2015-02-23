package org.aksw.cubeqa.property.scorer;

import java.util.Collections;
import org.aksw.cubeqa.property.ComponentProperty;

/** Abstract superclass for data type properties, whose values have literals. */
public abstract class DatatypePropertyScorer extends MultiSetScorer
{
	public DatatypePropertyScorer(ComponentProperty property)
	{
		super(property,node->Collections.<String>singleton(node.asLiteral().getLexicalForm()));
	}

}