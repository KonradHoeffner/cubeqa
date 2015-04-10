package org.aksw.cubeqa.index;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import lombok.SneakyThrows;
import org.aksw.cubeqa.property.ComponentProperty;
import org.apache.lucene.document.*;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.*;

/** Index for String scorer. */
public class StringIndex extends Index
{
	private static final Map<String,StringIndex> instances = new HashMap<>();
	private StringIndex(ComponentProperty property) {super(property);}
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


	@SneakyThrows
	public void fill(Set<String> strings)
	{
		if(!DirectoryReader.indexExists(dir))
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
		Map<String,Double> stringsWithScore = new HashMap<>();
		String ns=normalize(s);
		if(ns.isEmpty()) {return stringsWithScore;}

		Map<Query,String> queryFields = new HashMap<>();
		queryFields.put(parser.parse(ns),"textlabel");

		if(ns.length()>=FUZZY_MIN_LENGTH)
		{
			queryFields.put(new FuzzyQuery(new Term("stringlabel",ns)), "stringlabel");
		}

		int hitsPerPage = 10;
		IndexSearcher searcher = new IndexSearcher(reader);


		for(Query q: queryFields.keySet())
		{
			TopScoreDocCollector collector = TopScoreDocCollector.create(hitsPerPage);
			searcher.search(q, collector);
			ScoreDoc[] hits = collector.topDocs().scoreDocs;

			for(ScoreDoc hit: hits)
			{
				Document doc = searcher.doc(hit.doc);

				Arrays.stream(doc.getValues("originallabel")).filter(l->l.length()>3).forEach(
							l->stringsWithScore.put(l, (double)distance.getDistance(ns, normalize(l))));
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
		doc.add(new Field("originallabel", s, StringField.TYPE_STORED));
		indexWriter.addDocument(doc);
	}

}