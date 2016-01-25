package org.aksw.cubeqa.index;

import java.util.*;
import java.io.File;
import org.aksw.cubeqa.Cube;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import lombok.SneakyThrows;

/** Finds the right cube for a query. Implemented as Lucene index. Modified copy of {@link Index}.*/
public class CubeIndex
{
	//	{log.setLevel(Level.ALL);}
	//	protected static StringDistance distance = new NGramDistance();
	//	final public boolean isEmpty() {return !folder.exists();}

	private static final Analyzer analyzer = new EnglishAnalyzer();
	private static final int	NUMBER_OF_HITS	= 3;

	private IndexWriter indexWriter;
	private final Directory dir;
	private IndexReader reader;

	public static CubeIndex INSTANCE = new CubeIndex();

	@SneakyThrows
	private CubeIndex()
	{
		File folder = new File(new File(new File("cache"),"lucene"),"cubecache");
		folder.mkdirs();
		dir = FSDirectory.open(folder.toPath());
		if(DirectoryReader.indexExists(dir))
		{
			reader = DirectoryReader.open(dir);
		}
	}

	@SneakyThrows
	public synchronized void add(Cube cube)
	{
		if(indexWriter==null) {throw new IllegalStateException("indexWriter is null, call startWrites() first.");}
		Document doc = new Document();

		doc.add(new StringField("uri", cube.uri, Field.Store.YES));
		{
			TextField labelField = new TextField("label", Index.normalize(cube.label),Field.Store.NO);
			doc.add(labelField);
		}
		{
			TextField commentField = new TextField("comment", Index.normalize(cube.comment),Field.Store.NO);
			doc.add(commentField);
		}
		doc.add(new TextField("properties",
				Index.normalize(
						cube.properties.values().stream().flatMap(p->p.getLabels().stream()).reduce("", (a,b)->a+" "+b)),
				Field.Store.NO));

		indexWriter.addDocument(doc);
	}

	/** You can fill the index only once right now.*/
	@SneakyThrows public void fill(Set<Cube> cubes)
	{
		if(!DirectoryReader.indexExists(dir))
		{

			startWrites();
			cubes.stream().forEach(this::add);
			stopWrites();
		}
		reader = DirectoryReader.open(dir);
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
			IndexWriterConfig config = new IndexWriterConfig(analyzer);
			indexWriter = new IndexWriter(dir, config);
		}
	}

	@SneakyThrows
	public List<String> getCubeUris(String question)
	{
		List<String> cubes = new ArrayList<>(NUMBER_OF_HITS);
		MultiFieldQueryParser queryParser = new MultiFieldQueryParser(new String[] {"label", "comment","properties"},analyzer);
		IndexSearcher searcher = new IndexSearcher(reader);
		Query q = queryParser.parse(Index.normalize(question));

		TopScoreDocCollector collector = TopScoreDocCollector.create(NUMBER_OF_HITS);
		searcher.search(q, collector);
		ScoreDoc[] hits = collector.topDocs().scoreDocs;
		for(ScoreDoc hit: hits)
		{
			//			System.out.println(searcher.doc(hit.doc).get("uri"));
			//			System.out.println(searcher.doc(hit.doc).get("label"));
			//			System.out.println(searcher.doc(hit.doc).get("comment"));
			//			System.out.println(searcher.doc(hit.doc).get("properties"));
			cubes.add(searcher.doc(hit.doc).get("uri"));
		}
		return cubes;
	}

}