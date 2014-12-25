package org.aksw.autosparql.cube.property.scorer;

import java.util.Set;
import java.util.stream.Collectors;
import org.aksw.autosparql.cube.CubeSparql;
import org.aksw.autosparql.cube.property.ComponentProperty;
import com.hp.hpl.jena.rdf.model.RDFNode;
import de.konradhoeffner.commons.IteratorStream;

/** Scores object properties, which scores exclusively by count in relation to maxCount.*/
public class ObjectPropertyScorer extends MultiSetScorer
{
//	Set<String> labelToUri = new HashSet<>();

	public ObjectPropertyScorer(ComponentProperty property)
	{
		super(property,node->getLabels(property.cube.sparql,node));
	}

	public double unsafeScore(String value)
	{
		return countScore(values.count(value));
	}

	static protected Set<String> getLabels(CubeSparql sparql, RDFNode node)
	{
		String query = "select ?l {<"+node.asResource().getURI()+"> rdfs:label ?l}";
		return IteratorStream.stream(sparql.select(query)).map(qs->qs.get("l").asLiteral().getLexicalForm()).collect(Collectors.toSet());
	}
}