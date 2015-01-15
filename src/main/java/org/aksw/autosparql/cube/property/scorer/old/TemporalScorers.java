package org.aksw.autosparql.cube.property.scorer.old;

import java.time.Year;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.aksw.autosparql.cube.property.ComponentProperty;
import org.aksw.autosparql.cube.property.scorer.MultiSetScorer;
import org.aksw.autosparql.cube.property.scorer.ParseScorer;

/** Scorers that parse a string to some Java time related object, such as year or date. */
public class TemporalScorers
{
	// s.substring(0,5) would also work in most cases but this handles years before 1000,
	// after 9999 and with + or - in front as well
	static Pattern yearPattern = Pattern.compile("[+-]?[\\d]+");
	static public MultiSetScorer createYearScorer(ComponentProperty p)
	{
		return new ParseScorer<Year>(p,
				s->{
					Matcher m=yearPattern.matcher(s);
					m.find();
					return Year.parse(m.group(0));
				});

							//				s->Year.parse(s.replaceAll("\\+[0-9][0-9]:[0-9][0-9]","")));
	}
	//	static public Scorer createMonthScorer(ComponentProperty p) {return new ParseScorer<Month>(p,Month::parse);}
}
