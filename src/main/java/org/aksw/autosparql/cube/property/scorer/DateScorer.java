package org.aksw.autosparql.cube.property.scorer;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import lombok.extern.java.Log;
import org.aksw.autosparql.cube.property.ComponentProperty;
import com.hp.hpl.jena.rdf.model.RDFNode;

@Log
public class DateScorer extends DatatypePropertyScorer
{
	private static final double	MIN_PARSE_ERROR_SUCCESS	= 0.9;
	final long[] epochDays;
	final long firstToLast;
	final long maxScoreDist;
	Map<Long,String> epochDayToValue = new HashMap<>();
// TODO values may be lost here when using this for datetime instead of date due to overwriting when using days instead of seconds or millis
	/** @param date a String representing a date, e.g. 2014-12-19
	 * @return	an optional with epoch days (days since 1970-1-1) if parsing was successfull, else an empty optional. */
	static private Optional<Long> parse(String date)
	{
//		date = date.replaceAll("\\+[0-9][0-9]:[0-9][0-9]", ""); // remove time zone
		date = date.substring(0, "1999-01-23".length()); // only date, no time
//		try
//		{
			return Optional.of(LocalDate.parse(date).toEpochDay());
//		}
//		catch(DateTimeParseException e)
//		{
//			log.warning("could not parse date '"+date+"'");
//			return Optional.empty();
//			}
		}

	public DateScorer(ComponentProperty property)
	{
		super(property);
		epochDays= values.elementSet().stream().filter(s->!s.isEmpty()).map(DateScorer::parse).filter(Optional::isPresent)
				.mapToLong(Optional::get).sorted().toArray();
		if(epochDays.length==0)
		{
			log.warning(property+": no values for date scorer");
			firstToLast = 0;
			maxScoreDist = 0;
			return;
		}
		if((double)epochDays.length/values.elementSet().size() < MIN_PARSE_ERROR_SUCCESS) {throw new RuntimeException("too many errors, only "+epochDays.length+" of "+values.elementSet().size()+" successfully parsed.");};

		firstToLast = epochDays[epochDays.length-1]-epochDays[0];
		maxScoreDist = Math.min(firstToLast, 365*2);
	}

	@Override public Optional<ScoreResult> unsafeScore(String value)
	{
		if(epochDays.length==0) {return Optional.empty();}
		if(values.contains(value)) {return Optional.of(new ScoreResult(property,value, 1));}
//		long time = Instant.parse(value).toEpochMilli();
		long time = parse(value).get();
		long closest = closestValue(epochDays, time);
		long dist = Math.abs(time-closest);
		if(dist*2>firstToLast) return Optional.empty();
		if(firstToLast==0) return Optional.of(new ScoreResult(property,epochDayToValue.get(closest), 1));
		return Optional.of(new ScoreResult(property,epochDayToValue.get(closest), 1-((double)dist)/firstToLast));
	}

}