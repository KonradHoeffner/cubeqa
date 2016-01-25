package org.aksw.cubeqa.property.scorer;

import java.util.*;
import java.util.stream.Collectors;
import org.aksw.cubeqa.Config;
import org.aksw.cubeqa.index.LabelIndex;
import org.aksw.cubeqa.property.ComponentProperty;
import de.konradhoeffner.commons.Streams;

/** Scores object properties, which scores exclusively by count in relation to maxCount.*/
public class ObjectPropertyScorer extends MultiSetScorer
{
	transient LabelIndex index;
	private static final double	THRESHOLD	= 0.4;

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

	@Override public Optional<ScoreResult> score(String value)
	{
		loadOrCreateIndex();

		Map<String,Double> urisWithScore = index.getUrisWithScore(value,Config.INSTANCE.indexMinScore);

		return urisWithScore.keySet().stream()
				.filter(s->urisWithScore.get(s)>THRESHOLD)
				.max(Comparator.comparing(urisWithScore::get))
				.map(uri->new ScoreResult(property, uri, urisWithScore.get(uri)));
	}

	public Set<String> getLabels(String uri)
	{
		String query = "select ?l {<"+uri+"> rdfs:label ?l}";
		return Streams.stream(property.cube.sparql.select(query)).map(qs->qs.get("l").asLiteral().getLexicalForm()).collect(Collectors.toSet());
	}

}