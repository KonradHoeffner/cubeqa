package org.aksw.autosparql.cube.property.scorer;

import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.aksw.autosparql.cube.index.LabelIndex;
import org.aksw.autosparql.cube.property.ComponentProperty;
import de.konradhoeffner.commons.Streams;

/** Scores object properties, which scores exclusively by count in relation to maxCount.*/
public class ObjectPropertyScorer extends MultiSetScorer
{
	transient LabelIndex index;


	private synchronized void loadOrCreateIndex()
	{
		if(index==null)
		{
			index = LabelIndex.getInstance(property);
			index.fill(values.elementSet(), this::getLabels);
		}
	}

	public ObjectPropertyScorer(ComponentProperty property)
	{
		super(property,node->Collections.singleton(node.asResource().getURI()));
	}

	public Optional<ScoreResult> unsafeScore(String value)
	{
		loadOrCreateIndex();

		Map<String,Double> urisWithScore = index.getUrisWithScore(value);

		return urisWithScore.keySet().stream()
				.max(Comparator.comparing(urisWithScore::get))
				.map(uri->new ScoreResult(property, uri, urisWithScore.get(uri)));
	}

	public Set<String> getLabels(String uri)
	{
		String query = "select ?l {<"+uri+"> rdfs:label ?l}";
		return Streams.stream(property.cube.sparql.select(query)).map(qs->qs.get("l").asLiteral().getLexicalForm()).collect(Collectors.toSet());
	}

}