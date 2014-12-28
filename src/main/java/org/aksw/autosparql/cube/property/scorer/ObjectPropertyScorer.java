package org.aksw.autosparql.cube.property.scorer;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.aksw.autosparql.cube.property.ComponentProperty;
import de.konradhoeffner.commons.IteratorStream;
import de.konradhoeffner.commons.Pair;

/** Scores object properties, which scores exclusively by count in relation to maxCount.*/
public class ObjectPropertyScorer extends MultiSetScorer
{
		Map<String,String> labelToUri = new HashMap<String,String>();

	public ObjectPropertyScorer(ComponentProperty property)
	{
		super(property,node->Collections.singleton(node.asResource().getURI()));
		for(String uri: values.elementSet())
		{
			getLabels(uri).forEach(label->labelToUri.put(label, uri));
		}
//		super(property,node->getLabels(property.cube.sparql,node));
	}

	public Optional<ScoreResult> unsafeScore(String value)
	{
		Pair<String,Double> score = labelToUri.keySet().stream()
				.map(l->new Pair<String,Double>(l,(double)similarity.getSimilarity(l, value)))
		.max(Comparator.comparing(Pair::getB)).get();

		if(score.getB()<0.8) return Optional.empty();
		return Optional.of(new ScoreResult(property, labelToUri.get(score.getA()), score.getB()));
		//		return countScore(values.count(value));
	}

	public Set<String> getLabels(String uri)
	{
		String query = "select ?l {<"+uri+"> rdfs:label ?l}";
		return IteratorStream.stream(property.cube.sparql.select(query)).map(qs->qs.get("l").asLiteral().getLexicalForm()).collect(Collectors.toSet());
	}

}