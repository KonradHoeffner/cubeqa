package org.aksw.cubeqa.property.scorer;

import java.util.Comparator;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.aksw.cubeqa.index.StringIndex;
import org.aksw.cubeqa.property.ComponentProperty;
import org.apache.lucene.search.spell.NGramDistance;
import org.apache.lucene.search.spell.StringDistance;

/** Scorer for data type properties. */
public class StringScorer extends DatatypePropertyScorer
{
	private static final long	serialVersionUID	= 1L;
	protected static transient StringDistance similarity = new NGramDistance();

	transient StringIndex index;

	private synchronized void loadOrCreateIndex()
	{
		if(index==null)
		{
			index = StringIndex.getInstance(property);
			index.fill(valueStream().map(node->node.asLiteral().getLexicalForm()).collect(Collectors.toSet()));
		}
	}

	public StringScorer(ComponentProperty property)
	{
		super(property);
	}

	@Override
	public Optional<ScoreResult> unsafeScore(String value)
	{
		loadOrCreateIndex();

		Map<String,Double> stringsWithScore = index.getStringsWithScore(value);

		return stringsWithScore.keySet().stream()
				.max(Comparator.comparing(stringsWithScore::get))
				.map(s->new ScoreResult(property, s, stringsWithScore.get(s)));
	}

//	@Override public Optional<ScoreResult> unsafeScore(String value)
//	{
//		// TODO: wordnet,solr
//		//		double cs = countScore(values.count(value));
//		//		if(cs!=0) {return cs;}
//		if(values.count(value)>0) return Optional.of(new ScoreResult(property, value, 1));
//		double maxScore = 0;
//
//		String maxValue = null;
//		for(String s: values.elementSet())
//		{
//			double sim = similarity.getDistance(value, s);
//			if(sim<THRESHOLD) continue;
//
//			if(sim>maxScore)
//			{
//				maxScore = sim;
//				maxValue = s;
//			}
//			//			score = Math.max(score, sim*countScore(values.count(s)));
//		}
//		if(maxValue==null) {return Optional.empty();}
//		return Optional.of(new ScoreResult(property, maxValue, maxScore));
//		//		values.elementSet().stream().map(s->Similarity.getSimilarity(value, s)).filter(sim->sim>THRESHOLD)
//		//		.map(sim->sim*countScore(values.count(s),maxCount));
//	}
}