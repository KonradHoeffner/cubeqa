package org.aksw.cubeqa;

import org.kohsuke.args4j.Option;

public class Config
{
	@Option(name="-intervalMinSimilarity",usage="Sets the minimum similarity for named entity detection in intervals")
	public double intervalMinSimilarity = 0.3;

	@Option(name="-indexNonExactMatchMinLength")
	public int indexNonExactMatchMinLength = 6;

	@Option(name="-indexMinLuceneScore")
	public float indexMinLuceneScore = 3;

	@Option(name="-indexMinLuceneScore")
	public double indexMinScore = 0.4;

	@Option(name="-scorerPropertyNameMinScore")
	public double scorerPropertyNameMinScore	= 0.6;

	public enum IndexQueries {EXACT,FUZZY,ANALYZED,BOTH}

	@Option(name="-indexQueries")
	public IndexQueries indexQueries = IndexQueries.BOTH;

	@Option(name="-boostNumeric")
	public double boostNumeric = 0.98; // give precedence to years over date and other numbers
	public double boostDate = 0.99; // give precedence to years over date


//	@Option(name="-indexDoNonExactMatch")
//	public boolean	indexDoNonExactMatch = true;
//
//	@Option(name="-indexDoAnalyzedMatching")
//	public boolean	indexDoAnalyzedMatching = true;

	private Config() {}

	public static final Config INSTANCE = new Config();
}