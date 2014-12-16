package org.aksw.autosparql.cube.property.scorer;

import org.aksw.autosparql.cube.Cube;
import org.aksw.autosparql.cube.property.ComponentProperty;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import de.konradhoeffner.commons.IteratorStream;

public abstract class StringScorer extends Scorer
{
	final Multiset<String> values = HashMultiset.create();
	final int maxCount;

	public StringScorer(Cube cube, ComponentProperty property)
	{
		super(cube,property);

		IteratorStream.stream(queryValues()).forEach(qs->values.add(qs.get("value").asResource().getURI(), qs.get("cnt").asLiteral().getInt()));
		maxCount = values.elementSet().stream().map(s->values.count(s)).max(Integer::compare).get();
	}

}