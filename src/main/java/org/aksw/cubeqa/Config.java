package org.aksw.cubeqa;

import org.kohsuke.args4j.Option;

public enum Config
{
	INSTANCE;
	
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

	// change may require cache deletion to take effect
	@Option(name="-useManualLabels")
	public boolean useManualLabels = false;

	public boolean	removeStopWords = false;

	public boolean	useDefaultAnswerProperty = true;

	/** For values which are only referenced by value, not by property name.
	 Happens very often in practice (e.g. most people say "in 2010" and not "in the year of 2010") so I recommend to set the config parameter to true. */
	public boolean	findNamelessReferences = true;

	/** True, iff datasets from the benchmark are predetermined (algorithm doesn't have to search for it based on the query). */
	public boolean	givenDataSets= true;

	public boolean	useAnswerTypes = true;

//	@Option(name="-indexDoNonExactMatch")
//	public boolean	indexDoNonExactMatch = true;
//
//	@Option(name="-indexDoAnalyzedMatching")
//	public boolean	indexDoAnalyzedMatching = true;
}