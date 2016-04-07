package org.aksw.cubeqa.index;

import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.simmetrics.StringMetric;
import org.simmetrics.metrics.StringMetrics;
import lombok.SneakyThrows;

public class Similarity
{
	static final QueryParser parser = new QueryParser("",new EnglishAnalyzer());
	static final StringMetric metric = StringMetrics.qGramsDistance();

	/** @param s word or phrase or general string
	/** @param t another word or phrase or general string
	/** @return a similarity value between 0 (totally different) and 1 (exactly equal). applies stemming and lower case. */
	@SneakyThrows
	static public float similarity(String s, String t)
	{
		return metric.compare(parser.parse(s).toString(), parser.parse(t).toString());
	}
}
