package org.aksw.cubeqa.property.scorer;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Stream;
import lombok.extern.log4j.Log4j;
import org.aksw.cubeqa.CubeSparql;
import org.aksw.cubeqa.property.ComponentProperty;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.RDFNode;
import de.konradhoeffner.commons.Streams;

/** Scorers match phrases or words to component property values.
 * Scorers are used when a phrase does not match to a component property label.
 * Scorers return a similarity value in [0,1].
 */
@Log4j
public abstract class Scorer implements Serializable
{
	private static final long	serialVersionUID	= 1L;
	protected final ComponentProperty property;

	public Scorer(ComponentProperty property)
	{
		this.property=property;
	}

	abstract protected Optional<ScoreResult> unsafeScore(String value);

	/** @param phrase a word or phrase
	 * @return 	a pair similarity score in [0,1]*/
	public Optional<ScoreResult> score(String phrase)
	{
		try
		{
			return unsafeScore(phrase);
		}
		catch(Exception e)
		{
//			log.warn(e.getClass().getName()+": "+e.getMessage());
			return Optional.empty();
		}
	}

	protected ResultSet queryValues()
	{
		String query = "select ?value (count(?value) as ?cnt)"
				+ "{?obs a qb:Observation. ?obs <"+property.uri+"> ?value. } group by ?value";
		ResultSet rs = CubeSparql.linkedSpending(property.cube.name).select(query);
		return rs;
	}

	protected Stream<RDFNode> valueStream()
	{
		return Streams.stream(queryValues()).map(qs->qs.get("value"));
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