package org.aksw.cubeqa.index;

import java.io.IOException;
import java.util.*;
import lombok.SneakyThrows;
import org.aksw.cubeqa.property.ComponentProperty;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.util.Version;

/** Index for String scorer. */
public class StringIndex extends Index
{
	private static final Map<String,StringIndex> instances = new HashMap<>();
	private static final int	FUZZY_MIN_LENGTH	= 5;
	private Analyzer analyzer = new EnglishAnalyzer();
	private QueryParser parser = new QueryParser(Version.LUCENE_4_9_1,"textlabel", analyzer);

	private StringIndex(ComponentProperty property)
	{
		super(property);
	}

	@SneakyThrows
	public void fill(Set<String> strings)
	{
		if(!subFolder.exists())
		{
			startWrites(analyzer);
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
		String ns=normalize(s);

		List<Query> queries;
		if(ns.length()>=FUZZY_MIN_LENGTH)
		{
			queries = Arrays.asList(new FuzzyQuery(new Term("stringlabel",ns)),parser.parse(ns));
		} else
		{
			queries = Collections.singletonList(parser.parse(ns));
		}

		//		System.out.println(q);
		int hitsPerPage = 10;
		IndexSearcher searcher = new IndexSearcher(reader);
		TopScoreDocCollector collector = TopScoreDocCollector.create(hitsPerPage, true);

		Map<String,Double> stringsWithScore = new HashMap<>();

		for(Query q: queries)
		{
			searcher.search(q, collector);
			ScoreDoc[] hits = collector.topDocs().scoreDocs;

			for(ScoreDoc hit: hits)
			{
				// TODO score with fuzzy matches too high
				Document doc = searcher.doc(hit.doc);
				//			for(String l: doc.getValues("label"))
				//			{
				//				System.out.println(l+" "+label+" distance: "+distance.getDistance(label, l));
				//			}
				//TODO activate again and see why it works so badly
				double score = Arrays.stream(doc.getValues("normalizedlabel")).filter(nl->nl.length()>3).mapToDouble(l->(distance.getDistance(ns, l)*0.0)).max().getAsDouble();

				// Lucene returns document retrieval score instead of similarity score
				stringsWithScore.put(doc.get("label"),score);
				//			urisWithScore.put(doc.get("uri"),(double) hit.score);
			}
		}
		return stringsWithScore;
	}

	public void add(String s) throws IOException
	{
		if(indexWriter==null) throw new IllegalStateException("indexWriter is null, call startWrites() first.");
		Document doc = new Document();
		doc.add(new Field("stringlabel", normalize(s), StringField.TYPE_STORED));
		doc.add(new Field("textlabel", normalize(s), TextField.TYPE_STORED));
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