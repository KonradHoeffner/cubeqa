package org.aksw.autosparql.cube.index;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import lombok.SneakyThrows;
import org.aksw.autosparql.cube.property.ComponentProperty;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.KeywordAnalyzer;
import org.apache.lucene.analysis.miscellaneous.PerFieldAnalyzerWrapper;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.spell.NGramDistance;
import org.apache.lucene.search.spell.StringDistance;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

public abstract class Index
{
	// TODO: make sure instances for multiple cubes are not conflicting, property uris may not be unique
	protected final ComponentProperty property;

	protected final Analyzer analyzer;

	protected IndexWriter indexWriter;
	protected final Directory dir;
	protected IndexReader reader;

	protected static StringDistance distance = new NGramDistance();

	protected File subFolder;

	final public boolean isEmpty()
	{
		return !subFolder.exists();
	}

	@SneakyThrows
	protected Index(ComponentProperty property)
	{
		Map<String,Analyzer> analyzerPerField = new HashMap<String,Analyzer>();
//		analyzerPerField.put("normalizedlabel", new EnglishAnalyzer());

		analyzer = new PerFieldAnalyzerWrapper(new KeywordAnalyzer(), analyzerPerField);

		this.property=property;
		File folder = new File(new File("cache"),"lucene");
		folder.mkdirs();
		subFolder = new File(folder,property.shortName());

		dir = FSDirectory.open(subFolder);
	}

	static protected String normalize(String s)
	{
		return s.replace("&", "and").toLowerCase();
	}

	@SneakyThrows
	final protected synchronized void stopWrites()
	{
		indexWriter.close();
		indexWriter=null;
	}

	@SneakyThrows protected
	final synchronized void startWrites()
	{
		if(indexWriter==null)
		{
			IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_4_10_3, analyzer);
			indexWriter = new IndexWriter(dir, config);
		}
	}
}