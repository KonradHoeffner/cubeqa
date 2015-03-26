package org.aksw.cubeqa.index;

import java.io.IOException;
import java.util.*;
import lombok.SneakyThrows;
import org.aksw.cubeqa.property.ComponentProperty;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
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
	private StandardAnalyzer analyzer = new StandardAnalyzer();
	private QueryParser parser = new QueryParser(Version.LUCENE_4_9_1,"normalizedlabel", analyzer);

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
		String ns=normalize(s);

		Query q;
		if(ns.length()>=FUZZY_MIN_LENGTH)
		{
		q = new FuzzyQuery(new Term("normalizedlabel",ns));
		} else
		{
			q = parser.parse(ns);
		}
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
//TODO activate again and see why it works so badly
			double score = Arrays.stream(doc.getValues("normalizedlabel")).filter(nl->nl.length()>3).mapToDouble(l->(distance.getDistance(ns, l)*0.0)).max().getAsDouble();

			// Lucene returns document retrieval score instead of similarity score
			stringsWithScore.put(doc.get("label"),score);
			//			urisWithScore.put(doc.get("uri"),(double) hit.score);
		}
		return stringsWithScore;
	}

	public void add(String s) throws IOException
	{
		if(indexWriter==null) throw new IllegalStateException("indexWriter is null, call startWrites() first.");
		Document doc = new Document();
		doc.add(new StringField("label", s, Field.Store.YES));
		doc.add(new TextField("normalizedlabel", normalize(s), Field.Store.YES));
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