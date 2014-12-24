package org.aksw.autosparql.cube.property.scorer;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.Function;
import lombok.extern.java.Log;
import org.aksw.autosparql.cube.CubeSparql;
import org.aksw.autosparql.cube.property.ComponentProperty;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.RDFNode;
import de.konradhoeffner.commons.IteratorStream;

@Log
public abstract class Scorer implements Serializable
{
	final protected Multiset<String> values = HashMultiset.create();
	final protected int maxCount;

	private static final long	serialVersionUID	= 1L;
	final ComponentProperty property;

	public Scorer(ComponentProperty property, Function<RDFNode,String> f)
	{
		this.property=property;
		IteratorStream.stream(queryValues()).forEach(qs->values.add(f.apply(qs.get("value")), qs.get("cnt").asLiteral().getInt()));

		Optional<Integer> max = values.elementSet().stream().map(s->values.count(s)).max(Integer::compare);
		if(!max.isPresent())
		{
			log.warning("no values for property "+property+": "+values);
			maxCount=0;
		}
		else
		{
			maxCount = max.get();
		}
	}

	abstract protected double unsafeScore(String value);

	public double score(String value)
	{
		try
		{
			return unsafeScore(value);
		}
		catch(Exception e)
		{
//			log.warning(e.getClass().getName()+": "+e.getMessage());
			return 0;
		}
	}

	protected ResultSet queryValues()
	{
		String query = "select ?value (count(?value) as ?cnt)"
				+ "{?obs a qb:Observation. ?obs <"+property.uri+"> ?value. } group by ?value";
		ResultSet rs = CubeSparql.linkedSpending(property.cube.name).select(query);
		return rs;
	}

	protected double countScore(int count)
	{
		// +1 to prevent div by 0 the nearer the score to the max, the higher the value, but don't fall of too steep so use log.
		if(count==0) return 0;
		return Math.log(count+1)/Math.log(maxCount+1);
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
		//		if(ip>3) System.out.println(Arrays.toString(Arrays.copyOfRange(sorted,ip-2,ip+3)));
		float closest;
		if(sorted[ip]-key<key-sorted[ip-1])	{closest=sorted[ip];} // < can be <= if smaller value is preferred
		else							{closest=sorted[ip-1];}
		return closest;
	}

	static protected long closestValue(long[] sorted, long key)
	{
		if(sorted.length==1) {return sorted[0];}	// trivial case
		if(key<sorted[0]) {return sorted[0];}		// lower boundary
		if(key>sorted[sorted.length-1]) {return sorted[sorted.length-1];} // upper boundary
		int pos = Arrays.binarySearch(sorted, key);
		if(pos>=0) {return sorted[pos];} // we found an exact match
		// we didn't find an exact match, now we have two candidates: insertion point and insertion point-1 (we excluded the trivial case before)
		// pos = -ip-1 | +ip -pos => ip = -pos-1
		int ip = -pos-1;
		long closest;
		if(sorted[ip]-key<key-sorted[ip-1])	{closest=sorted[ip];} // < can be <= if smaller value is preferred
		else							{closest=sorted[ip-1];}
		return closest;
	}
}