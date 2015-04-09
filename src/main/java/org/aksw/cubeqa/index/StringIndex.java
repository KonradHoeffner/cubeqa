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
		String ns=normalize(s);
//		if(s.equals("Finnish Red Cross"))
//		{
//			System.out.println("frc");
//		}
		Map<Query,String> queryFields = new HashMap<>();
		queryFields.put(parser.parse(ns),"textlabel");

		if(ns.length()>=FUZZY_MIN_LENGTH)
		{
			queryFields.put(new FuzzyQuery(new Term("stringlabel",ns)), "stringlabel");
		}
		//		System.out.println(q);
		int hitsPerPage = 10;
		IndexSearcher searcher = new IndexSearcher(reader);

		Map<String,Double> stringsWithScore = new HashMap<>();

		for(Query q: queryFields.keySet())
		{
			TopScoreDocCollector collector = TopScoreDocCollector.create(hitsPerPage);
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
//				System.out.println(Arrays.toString(doc.getValues("stringlabel")));
//				System.out.println(Arrays.toString(doc.getValues("textlabel")));
//				System.out.println(Arrays.toString(doc.getValues("originallabel")));
//				Set<String> values = new HashSet<>(Arrays.asList(doc.getValues("stringlabel")));
//				values.addAll(doc.getValues("textlabel"));


				Arrays.stream(doc.getValues("originallabel")).filter(l->l.length()>3).forEach(
							l->stringsWithScore.put(l, (double)distance.getDistance(ns, normalize(l))));
//				System.out.println(stringsWithScore);
//				mapToDouble(l->(distance.getDistance(ns, l))).max().getAsDouble();


				// Lucene returns document retrieval score instead of similarity score
//				stringsWithScore.put(doc.get("label"),score);
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
		doc.add(new Field("originallabel", s, StringField.TYPE_STORED));
		indexWriter.addDocument(doc);
	}

}