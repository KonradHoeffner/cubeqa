package org.aksw.autosparql.cube.property;

import lombok.RequiredArgsConstructor;
import org.aksw.autosparql.cube.Cube;
import org.aksw.autosparql.cube.CubeSparql;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import com.hp.hpl.jena.query.ResultSet;
import de.konradhoeffner.commons.IteratorStream;

public class UriScorer
{
	final Cube cube;
//	final ComponentProperty property;

	final Multiset<String> values = HashMultiset.create();
	final int highestOccurrence;

	public UriScorer(Cube cube, ComponentProperty property)
	{
		this.cube = cube;

		String query = "select ?value (count(?value) as ?cnt)"
				+ "{?obs a qb:Observation. ?obs <"+property.uri+"> ?value. } group by ?value";
		ResultSet rs = CubeSparql.linkedSpending(cube.name).select(query);

		IteratorStream.stream(rs).forEach(qs->values.add(qs.get("value").asResource().getURI(), qs.get("cnt").asLiteral().getInt()));
		highestOccurrence = values.elementSet().stream().map(s->values.count(s)).max(Integer::compare).get();
	}

	double score(String value)
	{
		double c = values.count(value);
		if(c==0) return 0;
		// +1 to prevent div by 0 the nearer the score to the max, the higher the value, but don't fall of too steep so use log.
		return Math.log(c+1)/Math.log(highestOccurrence+1);
	}
}
