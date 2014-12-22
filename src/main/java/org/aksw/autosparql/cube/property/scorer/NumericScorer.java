package org.aksw.autosparql.cube.property.scorer;

import java.util.Arrays;
import org.aksw.autosparql.cube.property.ComponentProperty;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import com.google.common.primitives.Floats;
import de.konradhoeffner.commons.IteratorStream;

/** Scores numbers both based on proximity to nearest property value and on count. */
public class NumericScorer extends Scorer
{
	final Multiset<Float> values = HashMultiset.create();
	final int maxCount;
	final float[] sorted;

	public NumericScorer(ComponentProperty property)
	{
		super(property);
		IteratorStream.stream(queryValues()).forEach(qs->values.add(qs.get("value").asLiteral().getFloat(), qs.get("cnt").asLiteral().getInt()));
		maxCount = values.elementSet().stream().map(s->values.count(s)).max(Integer::compare).get();
		sorted = Floats.toArray(values.elementSet());
		Arrays.sort(sorted);
	}

	private float similarity(float v, float value)
	{
		// TODO steeper falloff
		// TODO incorporate number of occurrences
		double eps = 0.01;
		if(Math.abs(v-value)<eps) return 1;
		if(v==0&&value==0) return 1;
		if(v==0^value==0) return 0;
		return (float)Math.pow(Math.min(Math.abs(v/value),Math.abs(value/v)),4);
	}

	public double score(String value)
	{
		float f = Float.valueOf(value);
//		System.out.println("value: "+value);
		float closest = closestValue(sorted, f);
//		System.out.println("sim: "+similarity(f,closest));
//		System.out.println("count: "+countScore(values.count(closest),maxCount));
		return similarity(f,closest)*countScore(values.count(closest), maxCount);
	}
}