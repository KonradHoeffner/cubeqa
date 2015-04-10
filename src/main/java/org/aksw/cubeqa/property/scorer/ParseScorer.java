package org.aksw.cubeqa.property.scorer;

import java.time.Year;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import lombok.extern.log4j.Log4j;
import org.aksw.cubeqa.property.ComponentProperty;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multiset.Entry;

/** Parses string value using a given function.
 * @param <T> The type the string gets parsed to.*/
@Log4j
public class ParseScorer<T> extends DatatypePropertyScorer
{
	final Multiset<T> parsed = HashMultiset.create();
	Map<T,String> parsedToOriginal = new HashMap<>();
//	final Function<String,T> parseFunc;

	public ParseScorer(ComponentProperty property, Function<String,T> parseFunc)
	{
		super(property);
		List<String> unparseable = new LinkedList<>();
//		this.parseFunc = parseFunc;
		for(Entry<String> e: values.entrySet())
		{
			try
			{
				T parsedValue = parseFunc.apply(e.getElement());
				parsedToOriginal.put(parsedValue, e.getElement());
				parsed.add(parsedValue, e.getCount());
			}
			catch(Exception ex) {unparseable.add(e.getElement());}
		}
		if(!unparseable.isEmpty())
		{log.warn(property+": could not parse "+unparseable.size()+" of "+(unparseable.size()+parsed.size())+": "+unparseable);}
//		values.clear(); // from now on we only touch the integer years
	}

	@Override public Optional<ScoreResult> score(String value)
	{
		Year year = Year.parse(value.replaceAll("\\+[0-9][0-9]:[0-9][0-9]",""));
//		double cs = countScore(parsed.count(year));
		if(parsed.count(year)==0) return Optional.empty();
		return Optional.of(new ScoreResult(property, parsedToOriginal.get(parsed), 1));

		// TODO temporal bugfix, no serialization of function interface?
		//return countScore(parsed.count(parseFunc.apply(value)));
	}
}