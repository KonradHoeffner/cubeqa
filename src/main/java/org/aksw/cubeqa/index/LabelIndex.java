package org.aksw.cubeqa.index;

import java.io.IOException;
import java.util.*;
import java.util.function.Function;
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

/** Lucene index for labels, used by ObjectPropertyScorer.
 */
public class LabelIndex extends Index
{
	private static final Map<String,LabelIndex> instances = new HashMap<>();
	private static final int	FUZZY_MIN_LENGTH	= 5;
	//	private StandardAnalyzer analyzer = new StandardAnalyzer();
	private Analyzer analyzer = new EnglishAnalyzer();
	private QueryParser parser = new QueryParser(Version.LUCENE_4_9_1,"label", analyzer);

	private LabelIndex(ComponentProperty property)
	{
		super(property);
	}

	@SneakyThrows
	public void fill(Set<String> uris, Function<String,Set<String>> labelFunction)
	{
		if(!subFolder.exists())
		{
			startWrites(analyzer);
			for(String uri: uris)
			{
				//				Set<String> labels = labelFunction.apply(uri);
				//				System.out.println(labels);
				//				add(uri, labels);
				add(uri, labelFunction.apply(uri));
			}
			stopWrites();
		}
		reader = DirectoryReader.open(dir);
	}

	@SneakyThrows
	public Map<String,Double> getUrisWithScore(String label)
	{
		String ns = normalize(label);
		//		PhraseQuery q = new PhraseQuery();
		//		q.add(new Term("label",label));

		//		Query q = new QueryParser("label", analyzer).parse(querystr);

		List<Query> queries;
		if(ns.length()>=FUZZY_MIN_LENGTH)
		{
			queries = Arrays.asList(new FuzzyQuery(new Term("stringlabel",ns)),parser.parse(ns));
		} else
		{
			queries = Collections.singletonList(parser.parse(ns));
		}

		int hitsPerPage = 10;
		IndexSearcher searcher = new IndexSearcher(reader);
		TopScoreDocCollector collector = TopScoreDocCollector.create(hitsPerPage, true);
		Map<String,Double> urisWithScore = new HashMap<>();
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
				double score = Arrays.stream(doc.getValues("label")).mapToDouble(l->(distance.getDistance(ns, l))).max().getAsDouble();
				// Lucene returns document retrieval score instead of similarity score
				urisWithScore.put(doc.get("uri"),score);
				//			urisWithScore.put(doc.get("uri"),(double) hit.score);
			}
		}
		return urisWithScore;
	}

	public void add(String uri, Set<String> labels) throws IOException
	{
		if(indexWriter==null) throw new IllegalStateException("indexWriter is null, call startWrites() first.");
		Document doc = new Document();
		doc.add(new StringField("uri", uri, Field.Store.YES));
		//		doc.add(new TextField("cube", cube.name, Field.Store.YES));
		//		doc.add(new TextField("property", cube.name, Field.Store.YES));

		labels.forEach(l->
		{
			doc.add(new Field("stringlabel", normalize(l), StringField.TYPE_STORED));
			doc.add(new Field("textlabel", normalize(l), TextField.TYPE_STORED));
		});
		indexWriter.addDocument(doc, analyzer);
	}

	public static synchronized LabelIndex getInstance(ComponentProperty property)
	{
		LabelIndex index = instances.get(property.uri);
		if(index==null)
		{
			index = new LabelIndex(property);
			instances.put(property.uri,index);
		}
		return index;
	}

}