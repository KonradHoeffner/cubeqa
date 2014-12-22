package org.aksw.autosparql.cube.property.scorer;

import org.aksw.autosparql.cube.Cube;
import org.aksw.autosparql.cube.property.ComponentProperty;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import de.konradhoeffner.commons.IteratorStream;

/** Abstract superclass for data type properties, whose values have literals. */
public abstract class DatatypePropertyScorer extends Scorer
{
	final Multiset<String> values = HashMultiset.create();
	final int maxCount;

	public DatatypePropertyScorer(ComponentProperty property)
	{
		super(property);

		IteratorStream.stream(queryValues()).forEach(qs->values.add(qs.get("value").asLiteral().getLexicalForm(), qs.get("cnt").asLiteral().getInt()));
		maxCount = values.elementSet().stream().map(s->values.count(s)).max(Integer::compare).get();
	}

}