package org.aksw.cubeqa.benchmark;

import java.util.Set;
import java.util.stream.Collectors;
import com.hp.hpl.jena.query.ResultSet;
import de.konradhoeffner.commons.Pair;
import de.konradhoeffner.commons.Streams;

public class ResultSetPrecRec
{
	// TODO finish implementing, first for one dimensions, then for more
	/** @param rs the result set to be tested
	 * @param correct the correct output
	 * @return a pair (precision,recall) where precision is |rs cap correct| / |rs| and recall is |rs cap correct| / |correct|  */
	// for now only with the first column
	Pair<Double,Double> precRec(ResultSet rs, ResultSet correct)
	{
		Set<String> rsValues = Streams.stream(rs).map(qs->qs.get(qs.varNames().next()).asLiteral().getLexicalForm()).collect(Collectors.toSet());
		Set<String> correctValues = Streams.stream(rs).map(qs->qs.get(qs.varNames().next()).asLiteral().getLexicalForm()).collect(Collectors.toSet());
		return null;
	}

	static final double ECLUSION_QUOTIENT = 1.01;
	static final double INCLUSION_DIFFERENCE = 0.01;


	boolean identicalNumbers(String a, String b)
	{
		double d = Double.valueOf(a);
		double e = Double.valueOf(b);
		if(Math.abs(d-e)<INCLUSION_DIFFERENCE) {return true;}
		if(d!=0&&e!=0)
		{
		if(Math.max(Math.abs(d/e), Math.abs(e/d))<ECLUSION_QUOTIENT) return true;
		}
		return false;
	}

}