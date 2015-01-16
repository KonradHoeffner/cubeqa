package org.aksw.autosparql.cube.index;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import lombok.SneakyThrows;
import org.aksw.autosparql.cube.property.ComponentProperty;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.KeywordAnalyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.miscellaneous.PerFieldAnalyzerWrapper;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.FuzzyQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.search.spell.NGramDistance;
import org.apache.lucene.search.spell.StringDistance;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

public class StringIndex extends Index
{
	private static final Map<String,StringIndex> instances = new HashMap<>();

	@SneakyThrows
	private StringIndex(ComponentProperty property)
	{
		super(property);
	}

	@SneakyThrows
	public void fill(Set<String> strings)
	{
		if(!subFolder.exists())
		{
			startWrites();
			for(String s: strings)
			{
				add(s);
			}
			stopWrites();
		}
		reader = DirectoryReader.open(dir);
	}

	@SneakyThrows
	public Map<String,Double> getStringsWithScore(String s)
	{
		Query q = new FuzzyQuery(new Term("string",s));
//		System.out.println(q);
		int hitsPerPage = 10;
		IndexSearcher searcher = new IndexSearcher(reader);
		TopScoreDocCollector collector = TopScoreDocCollector.create(hitsPerPage, true);
		searcher.search(q, collector);
		ScoreDoc[] hits = collector.topDocs().scoreDocs;

		Map<String,Double> stringsWithScore = new HashMap<>();
		for(ScoreDoc hit: hits)
		{
			// TODO score with fuzzy matches too high
			Document doc = searcher.doc(hit.doc);
			//			for(String l: doc.getValues("label"))
			//			{
			//				System.out.println(l+" "+label+" distance: "+distance.getDistance(label, l));
			//			}
			double score = Arrays.stream(doc.getValues("string")).mapToDouble(l->(distance.getDistance(s, l))).max().getAsDouble();
			// Lucene returns document retrieval score instead of similarity score
			stringsWithScore.put(doc.get("uri"),score);
			//			urisWithScore.put(doc.get("uri"),(double) hit.score);
		}
		return stringsWithScore;
	}

	public void add(String s) throws IOException
	{
		if(indexWriter==null) throw new IllegalStateException("indexWriter is null, call startWrites() first.");
		Document doc = new Document();
		doc.add(new TextField("string", s, Field.Store.YES));
		indexWriter.addDocument(doc, analyzer);
	}

	public static synchronized StringIndex getInstance(ComponentProperty property)
	{
		StringIndex index = instances.get(property.uri);
		if(index==null)
		{
			index = new StringIndex(property);
			instances.put(property.uri,index);
		}
		return index;
	}

}