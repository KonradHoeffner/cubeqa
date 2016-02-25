package org.aksw.cubeqa.property.scorer.temporal;

import java.time.Year;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.aksw.cubeqa.Config;
import org.aksw.cubeqa.property.ComponentProperty;
import org.aksw.cubeqa.property.scorer.ScoreResult;
import org.aksw.cubeqa.property.scorer.Scorer;
import org.joda.time.Instant;
import org.joda.time.Interval;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.RDFNode;

/** Scorer for temporal intervals. */
@Slf4j
public class TemporalScorer extends Scorer
{
	private static final long	MS_PER_DAY	= 24*3600*1000;
	static Pattern yearPattern = Pattern.compile("^[+-]?[\\d]+");

	protected Set<Interval> intervals = new HashSet<>();

	/** Use when the property has datatype of year, e.g. xsd:gYear. */
	public static TemporalScorer yearScorer(ComponentProperty property)
	{
		return new TemporalScorer(property,TemporalScorer::parseAsYear);
	}

	/** Use when the property has datatype of date, e.g. xsd:date. */
	public static TemporalScorer dateScorer(ComponentProperty property)
	{
		return new TemporalScorer(property,TemporalScorer::parseAsDate);
	}

	protected TemporalScorer(ComponentProperty property,Function<String,Interval> parse)
	{
		super(property);
		Set<String> unparseable = new HashSet<>();
		valueStream().map(RDFNode::asLiteral).map(Literal::getLexicalForm).forEach(s->
		{
			try {intervals.add(parse.apply(s));}
			catch(Exception e ) {if(unparseable.size()<10) unparseable.add(s);}
		});
		if(!unparseable.isEmpty()) {log.warn("could not parse years "+unparseable);}
	}

	@Override public Optional<ScoreResult> score(String value)
	{
		Interval questionInterval = null;
		try
		{
			questionInterval = parseAsDate(value);
		}
		catch(IllegalArgumentException | IllegalStateException | StringIndexOutOfBoundsException | DateTimeParseException e)
		{
			try
			{
				questionInterval = parseAsYear(value);
			}
			catch (IllegalArgumentException | IllegalStateException | StringIndexOutOfBoundsException | DateTimeParseException f) {return Optional.empty();}
		}
		for(Interval interval: intervals)
		{
			if(interval.equals(questionInterval)||questionInterval.contains(interval))
			{
				double score = 1;
				if(property.range.equals("http://www.w3.org/2001/XMLSchema#date")) score = Config.INSTANCE.boostDate;
				return Optional.of(new ScoreResult(property, value, score));
			}
		}
		return Optional.empty();
	}

	static protected Interval parseAsYear(String s) throws IllegalArgumentException, IllegalStateException, StringIndexOutOfBoundsException, DateTimeParseException
	{
		Matcher m = yearPattern.matcher(s.trim());
		m.find();
		Year y = Year.parse(m.group(0));
		// yoda intervals are left closed, right open
		return new Interval(Instant.parse(y.getValue()+"-01-01").getMillis(), Instant.parse((y.getValue()+1)+"-01-01").getMillis());
	}

	static protected Interval parseAsDate(String s) throws IllegalArgumentException, IllegalStateException, StringIndexOutOfBoundsException, DateTimeParseException
	{
		s = s.trim().substring(0, "1999-01-23".length()); // only date, no time
		return new Interval(Instant.parse(s).getMillis(),Instant.parse(s).getMillis()+MS_PER_DAY);
	}

}