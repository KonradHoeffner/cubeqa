package org.aksw.autosparql.cube.property.scorer;

import org.aksw.autosparql.cube.Cube;
import org.aksw.autosparql.cube.property.ComponentProperty;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import de.konradhoeffner.commons.IteratorStream;

/** Abstract superclass for data type properties, whose values have literals. */
public abstract class DatatypePropertyScorer extends Scorer
{
	public DatatypePropertyScorer(ComponentProperty property)
	{
		super(property,r->r.asLiteral().getLexicalForm());
	}

}