package org.aksw.autosparql.cube.property.scorer;

import java.time.Year;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import lombok.extern.java.Log;
import org.aksw.autosparql.cube.property.ComponentProperty;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multiset.Entry;

@Log
public class ParseScorer<T> extends DatatypePropertyScorer
{
	final Multiset<T> parsed = HashMultiset.create();
//	final Function<String,T> parseFunc;

	public ParseScorer(ComponentProperty property, Function<String,T> parseFunc)
	{
		super(property);
		List<String> unparseable = new LinkedList<>();
//		this.parseFunc = parseFunc;
		for(Entry<String> e: values.entrySet())
		{
			try{parsed.add(parseFunc.apply(e.getElement()), e.getCount());}
			catch(Exception ex) {unparseable.add(e.getElement());}
		}
		if(!unparseable.isEmpty())
		{log.warning(property+": could not parse "+unparseable.size()+" of "+(unparseable.size()+parsed.size())+": "+unparseable);}
		values.clear(); // from now on we only touch the integer years
	}

	public double score(String value)
	{
		return countScore(parsed.count(Year.parse(value.replaceAll("\\+[0-9][0-9]:[0-9][0-9]",""))));
		// TODO temporal bugfix, no serialization of function interface?
		//return countScore(parsed.count(parseFunc.apply(value)));
	}
}