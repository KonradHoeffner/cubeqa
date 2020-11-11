package org.aksw.cubeqa;

import java.io.File;

import org.kohsuke.args4j.Option;

public enum Config
{
	INSTANCE;

	@Option(name="-folder",metaVar="<folder>",usage="Output folder where CubeQA will put its cache and other files. The default value is local to the program if running outside a jar, otherwise $tempdir/cubeqa.")
	public File folder;
	
	@Option(name="-endpoint",usage="SPARQL endpoint URL")
	public String endpoint = "http://linkedspending.aksw.org/sparql";

	@Option(name="-graphs",usage="space separated graph list")
	public String[] graphs = {"http://linkedgeodata.org/","http://linkedspending.aksw.org/","http://dbpedia.org"};

	@Option(name="-intervalMinSimilarity",usage="Sets the minimum similarity for named entity detection in intervals")
	public double intervalMinSimilarity = 0.3;

	@Option(name="-indexNonExactMatchMinLength")
	public int indexNonExactMatchMinLength = 6;

	@Option(name="-indexMinLuceneScore")
	public float indexMinLuceneScore = 3;

	@Option(name="-indexMinScore")
	public double indexMinScore = 0.4;

	public double placeMinScore = 0.7;
	
	@Option(name="-scorerPropertyNameMinScore")
	public double scorerPropertyNameMinScore	= 0.6;

	public enum IndexQueries {EXACT,FUZZY,ANALYZED,BOTH}

	@Option(name="-indexQueries")
	public IndexQueries indexQueries = IndexQueries.BOTH;

	@Option(name="-boostTemporal")
	public double boostTemporal = 0.99;
	@Option(name="-boostNumeric")
	public double boostNumeric = 0.98;
	@Option(name="-boostString")
	public double boostString = 0.95;

	// change may require cache deletion to take effect
	@Option(name="-useManualLabels")
	public boolean useManualLabels = false;

	@Option(name="-useCubeCache")
	public boolean USE_CUBE_CACHE = true;

	public boolean	removeStopWords = true;

	public boolean	useDefaultAnswerProperty = true;

	/** For values which are only referenced by value, not by property name.
	 Happens very often in practice (e.g. most people say "in 2010" and not "in the year of 2010") so I recommend to set the config parameter to true. */
	public boolean	findNamelessReferences = true;

	/** True, iff datasets from the benchmark are predetermined (algorithm doesn't have to search for it based on the query, leading to higher performance). */
	public boolean	givenDataSets= true;

	public boolean	useAnswerTypes = true;

	//	@Option(name="-indexDoNonExactMatch")
	//	public boolean	indexDoNonExactMatch = true;
	//
	//	@Option(name="-indexDoAnalyzedMatching")
	//	public boolean	indexDoAnalyzedMatching = true;
}