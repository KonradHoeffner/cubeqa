package org.aksw.autosparql.cube.property.scorer;

import org.aksw.autosparql.cube.property.ComponentProperty;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import de.konradhoeffner.commons.IteratorStream;

/** Scores object properties, which scores exclusively by count in relation to maxCount.*/
public class ObjectPropertyScorer extends Scorer
{


	public ObjectPropertyScorer(ComponentProperty property)
	{
		super(property,node->node.asResource().getURI());
	}

	public double unsafeScore(String value)
	{
		return countScore(values.count(value));
	}
}