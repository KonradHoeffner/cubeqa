package org.aksw.autosparql.cube.property.scorer.old;
//package org.aksw.autosparql.cube.property.scorer;
//
//import java.time.Year;
//import java.util.Optional;
//import lombok.extern.java.Log;
//import org.aksw.autosparql.cube.property.ComponentProperty;
//import com.google.common.collect.HashMultiset;
//import com.google.common.collect.Multiset;
//import com.google.common.collect.Multiset.Entry;
//import com.hp.hpl.jena.sparql.util.DateTimeStruct.DateTimeParseException;
//
//@Log
//public class YearScorerOld extends DatatypePropertyScorer
//{
//	final Multiset<Integer> years = HashMultiset.create();
//
//	Optional<Integer> parseYear(String s)
//	{
//		try{return Optional.of(Year.parse(s).getValue());}
//		catch(DateTimeParseException e) {log.warning("Could not parse year "+s);return Optional.empty();}
//	}
//
//	public YearScorerOld(ComponentProperty property)
//	{
//		super(property);
//		for(Entry<String> e: values.entrySet())
//		{
//			parseYear(e.getElement()).ifPresent(y->years.add(y, e.getCount()));
//		}
//		values.clear(); // from now on we only touch the integer years
//	}
//
//	public double score(String value)
//	{
//		return countScore(years.count(parseYear(value).get()),maxCount);
//	}
//}