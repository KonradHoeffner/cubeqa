package org.aksw.cubeqa.property.scorer.temporal;

import java.time.Year;
import java.time.format.DateTimeParseException;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.extern.log4j.Log4j;
import org.aksw.cubeqa.property.ComponentProperty;
import org.aksw.cubeqa.property.scorer.ScoreResult;
import org.aksw.cubeqa.property.scorer.Scorer;
import org.joda.time.Instant;
import org.joda.time.Interval;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.RDFNode;

/** Scorer for temporal intervals. */
@Log4j
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


	@Override protected Optional<ScoreResult> unsafeScore(String value)
	{
		Interval t = null;
		try
		{
			t = parseAsDate(value);
		} catch(DateTimeParseException e)
		{
			t = parseAsYear(value);
		}
		for(Interval interval: intervals)
		{
			if(interval.contains(t))
			{
				Optional.of(new ScoreResult(property, value, 1));
			}
		}
		return Optional.empty();
	}

	static protected Interval parseAsYear(String s)
	{
		Matcher m = yearPattern.matcher(s.trim());
		m.find();
		Year y = Year.parse(m.group(0));
		// yoda intervals are left closed, right open
		return new Interval(Instant.parse(y.getValue()+"-01-01").getMillis(), Instant.parse((y.getValue()+1)+"-01-01").getMillis());
	}

	static protected Interval parseAsDate(String s)
	{
		s = s.trim().substring(0, "1999-01-23".length()); // only date, no time
		return new Interval(Instant.parse(s).getMillis(),Instant.parse(s).getMillis()+MS_PER_DAY);
	}

}