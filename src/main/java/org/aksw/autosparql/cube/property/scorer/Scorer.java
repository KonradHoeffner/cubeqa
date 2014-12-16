package org.aksw.autosparql.cube.property.scorer;

import lombok.RequiredArgsConstructor;
import org.aksw.autosparql.cube.Cube;
import org.aksw.autosparql.cube.CubeSparql;
import org.aksw.autosparql.cube.property.ComponentProperty;
import com.hp.hpl.jena.query.ResultSet;

@RequiredArgsConstructor
public abstract class Scorer
{
	final Cube cube;
	final ComponentProperty property;

	abstract double score(String value);

	protected ResultSet queryValues()
	{
		String query = "select ?value (count(?value) as ?cnt)"
				+ "{?obs a qb:Observation. ?obs <"+property.uri+"> ?value. } group by ?value";
		ResultSet rs = CubeSparql.linkedSpending(cube.name).select(query);
		return rs;
	}
}