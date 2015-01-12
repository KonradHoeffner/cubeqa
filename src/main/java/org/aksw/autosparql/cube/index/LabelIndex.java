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
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.search.spell.NGramDistance;
import org.apache.lucene.search.spell.StringDistance;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

public class LabelIndex
{
	// TODO: make sure instances for multiple cubes are not conflicting, property uris may not be unique
	private final ComponentProperty property;

	private final StandardAnalyzer analyzer = new StandardAnalyzer();
	private IndexWriter indexWriter;
	private final Directory dir;
	private IndexReader reader;
	private StringDistance distance = new NGramDistance();

	private static final Map<String,LabelIndex> instances = new HashMap<>();

	private File subFolder;

	public boolean isEmpty()
	{
		return !subFolder.exists();
	}

	@SneakyThrows
	private LabelIndex(ComponentProperty property)
	{
		this.property=property;
		File folder = new File(new File("cache"),"lucene");
		folder.mkdirs();
		subFolder = new File(folder,property.shortName());

		dir = FSDirectory.open(subFolder);
	}

	@SneakyThrows
	public void fill(Set<String> uris, Function<String,Set<String>> labelFunction)
	{
		if(!subFolder.exists())
		{
			startWrites();
			for(String uri: uris)
			{
				add(uri, labelFunction.apply(uri));
			}
			stopWrites();
		}
		reader = DirectoryReader.open(dir);
	}

	@SneakyThrows
	public Map<String,Double> getUrisWithScore(String label)
	{
		String querystr= label+"~0.01";
		Query q = new QueryParser("label", analyzer).parse(querystr);

		int hitsPerPage = 10;
		IndexSearcher searcher = new IndexSearcher(reader);
		TopScoreDocCollector collector = TopScoreDocCollector.create(hitsPerPage, true);
		searcher.search(q, collector);
		ScoreDoc[] hits = collector.topDocs().scoreDocs;

		Map<String,Double> urisWithScore = new HashMap<>();
		for(ScoreDoc hit: hits)
		{
			// TODO score with fuzzy matches too high
			Document doc = searcher.doc(hit.doc);
//			for(String l: doc.getValues("label"))
//			{
//				System.out.println(l+" "+label+" distance: "+distance.getDistance(label, l));
//			}
			double score = Arrays.stream(doc.getValues("label")).mapToDouble(l->(distance.getDistance(label, l))).max().getAsDouble();
			// Lucene returns document retrieval score instead of similarity score
			urisWithScore.put(doc.get("uri"),score);
//			urisWithScore.put(doc.get("uri"),(double) hit.score);
		}
		return urisWithScore;
	}


	public void add(String uri, Set<String> labels) throws IOException
	{
		if(indexWriter==null) throw new IllegalStateException("indexWriter is null, call startWrites() first.");
		Document doc = new Document();
		doc.add(new TextField("uri", uri, Field.Store.YES));
		//		doc.add(new TextField("cube", cube.name, Field.Store.YES));
		//		doc.add(new TextField("property", cube.name, Field.Store.YES));
		labels.forEach(
				l-> doc.add(new StringField("label", l, Field.Store.YES)));
		indexWriter.addDocument(doc);
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

	@SneakyThrows
	private synchronized void stopWrites()
	{
		indexWriter.close();
		indexWriter=null;
	}

	@SneakyThrows
	private synchronized void startWrites()
	{
		if(indexWriter==null)
		{
			IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_4_10_3, analyzer);
			indexWriter = new IndexWriter(dir, config);
		}
	}
}