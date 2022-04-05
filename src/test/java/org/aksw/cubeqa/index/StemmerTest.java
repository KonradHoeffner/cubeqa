package org.aksw.cubeqa.index;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

public class StemmerTest
{
	EnglishAnalyzer en_an = new EnglishAnalyzer();
	QueryParser parser = new QueryParser("", en_an);

	@Test public void stemmStrengtheningTest() throws ParseException
	{
		assertEquals(parser.parse("strengthening civil society"),parser.parse("strengthen civil society"));
	}
	
	// expected to fail, Lucene English stemmer not aggressive enough
	@Disabled @Test public void stemmEgyptianTest() throws ParseException
	{
		assertEquals(parser.parse("egyptian"),parser.parse("egypt"));
	}
}