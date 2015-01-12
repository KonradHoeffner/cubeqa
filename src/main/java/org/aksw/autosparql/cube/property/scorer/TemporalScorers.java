package org.aksw.autosparql.cube.property.scorer;

import java.time.Year;
import org.aksw.autosparql.cube.property.ComponentProperty;

public class TemporalScorers
{
	static public MultiSetScorer createYearScorer(ComponentProperty p)
	{
		return new ParseScorer<Year>(p,
				s->Year.parse(s.replaceAll("\\+[0-9][0-9]:[0-9][0-9]","")));
	}
	//	static public Scorer createMonthScorer(ComponentProperty p) {return new ParseScorer<Month>(p,Month::parse);}
}
