package org.aksw.cubeqa.benchmark;

import java.util.HashSet;
import java.util.Set;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;

/** Represents and calculates precision, recall and f-score. */
@RequiredArgsConstructor
@EqualsAndHashCode
@Log4j
public class Performance
{
	final double precision;
	final double recall;

	public static final Performance performance(Set correct, Set found)
	{
		Set correctFound = new HashSet(found);
		correctFound.retainAll(correct);
		return performance(correct.size(),found.size(),correctFound.size());
	}

	public static final Performance performance(int correct, int found, int correctFound)
	{
		if(correct==0)
			{
//			throw new IllegalArgumentException("correct==0");
			log.fatal("no correct answer");
			return new Performance(0, 0);
			}
		if(found==0) return new Performance(0,0);
		return new Performance((double)correctFound/found,(double)correctFound/correct);
	}

	double fscore() {return fscore(1);}

	double fscore(double beta)
	{
		return (1+beta*beta)*(precision*recall)/(beta*beta*precision+recall);
	}

	@Override public String toString()
	{
	return "Performance(precision="+precision+", recall="+recall+", f-score="+fscore()+")";
	}
}