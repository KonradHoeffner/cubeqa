package org.aksw.autosparql.cube.property.scorer;

import java.util.Optional;
import org.aksw.autosparql.cube.property.ComponentProperty;
import org.aksw.autosparql.tbsl.algorithm.util.Similarity;

public class StringScorer extends DatatypePropertyScorer
{
	private static final double	THRESHOLD	= 0.8;

	public StringScorer(ComponentProperty property)
	{
		super(property);
	}

	@Override public Optional<ScoreResult> unsafeScore(String value)
	{
		// TODO: wordnet,solr
		//		double cs = countScore(values.count(value));
		//		if(cs!=0) {return cs;}
		if(values.count(value)>0) return Optional.of(new ScoreResult(property, value, 1));
		double maxScore = 0;

		String maxValue = null;
		for(String s: values.elementSet())
		{
			double sim = Similarity.getSimilarity(value, s);
			if(sim<THRESHOLD) continue;

			if(sim>maxScore)
			{
				maxScore = sim;
				maxValue = s;
			}
			//			score = Math.max(score, sim*countScore(values.count(s)));
		}
		if(maxValue==null) {return Optional.empty();}
		return Optional.of(new ScoreResult(property, maxValue, maxScore));
		//		values.elementSet().stream().map(s->Similarity.getSimilarity(value, s)).filter(sim->sim>THRESHOLD)
		//		.map(sim->sim*countScore(values.count(s),maxCount));
	}
}