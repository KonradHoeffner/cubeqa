package org.aksw.autosparql.cube.property.scorer;

import java.util.ArrayList;
import java.util.Arrays;
import org.aksw.autosparql.cube.Cube;
import org.aksw.autosparql.cube.property.ComponentProperty;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import de.konradhoeffner.commons.IteratorStream;

public class NumericScorer extends Scorer
{
	final Multiset<Float> values = HashMultiset.create();

	public NumericScorer(Cube cube, ComponentProperty property)
	{
		super(cube,property);
		IteratorStream.stream(queryValues()).forEach(qs->values.add(qs.get("value").asLiteral().getFloat(), qs.get("cnt").asLiteral().getInt()));
	}

	public double score(String value)
	{
return 0;
	}

	static protected float closestValue(float[] sorted, float key)
	{
		if(sorted.length==1) {return sorted[0];}	// trivial case
		if(key<sorted[0]) {return sorted[0];}		// lower boundary
		if(key>sorted[sorted.length-1]) {return sorted[sorted.length-1];} // upper boundary
		int pos = Arrays.binarySearch(sorted, key);
		if(pos>=0) {return sorted[pos];} // we found an exact match
		// we didn't find an exact match, now we have two candidates: insertion point and insertion point-1 (we excluded the trivial case before)
		// pos = -ip-1 | +ip -pos => ip = -pos-1
		int ip = -pos-1;
		float closest;
		if(sorted[ip]-key<key-sorted[ip-1])	{closest=sorted[ip];} // < can be <= if smaller value is preferred
		else							{closest=sorted[ip-1];}
		return closest;
	}
}