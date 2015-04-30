package org.aksw.cubeqa.index;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j;
import org.aksw.cubeqa.Config;
import org.aksw.cubeqa.property.ComponentProperty;
import org.apache.log4j.Level;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.search.spell.NGramDistance;
import org.apache.lucene.search.spell.StringDistance;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

/** Lucene index reading and writing abstract superclass. */
@Log4j
public abstract class Index
{
//	{log.setLevel(Level.ALL);}
	protected static final Analyzer analyzer = new EnglishAnalyzer();
	protected static final QueryParser parser = new QueryParser("textlabel", analyzer);
	private static final int	NUMBER_OF_HITS	= 5;
	// TODO: make sure instances for multiple cubes are not conflicting, property uris may not be unique
	protected final ComponentProperty property;

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
		this.property=property;
		File folder = new File(new File(new File("cache"),"lucene"),property.cube.probablyUniqueAsciiId());
		folder.mkdirs();
		subFolder = new File(folder,property.shortName());
		dir = FSDirectory.open(subFolder.toPath());
	}

	static protected String normalize(String s)
	{
		// parser crashes on some special characters such as ? as they are interpreted as wild cards
		return s.replace("&", "and").replaceAll("[^A-Za-z0-9 ]", "").toLowerCase().trim();
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
	protected Map<String,Double> getIdWithScore(String s, String fieldName, double minScore)
	{
		Map<String,Double> idWithScore = new HashMap<>();
		String ns=normalize(s);
		if(ns.isEmpty()) {return idWithScore;}

		List<Query> queries = new LinkedList<>();
		if((Config.INSTANCE.indexQueries==Config.IndexQueries.FUZZY||Config.INSTANCE.indexQueries==Config.IndexQueries.BOTH)
				&&ns.length()>=Config.INSTANCE.indexNonExactMatchMinLength)
		{
			queries.add(new FuzzyQuery(new Term("stringlabel",ns)));
		}
		if(Config.INSTANCE.indexQueries==Config.IndexQueries.ANALYZED||Config.INSTANCE.indexQueries==Config.IndexQueries.BOTH)
		{
			queries.add(parser.parse(ns));
		}

		IndexSearcher searcher = new IndexSearcher(reader);

		for(Query q: queries)
		{
			TopScoreDocCollector collector = TopScoreDocCollector.create(NUMBER_OF_HITS);
			searcher.search(q, collector);
			ScoreDoc[] hits = collector.topDocs().scoreDocs;
			Map<String,Double> idWithUnnormalizedScore = new HashMap<>();
			boolean fuzzy = (q instanceof FuzzyQuery);
			for(ScoreDoc hit: hits)
			{
				Document doc = searcher.doc(hit.doc);
				log.trace("Query "+q+" results in "+Arrays.toString(doc.getValues("originallabel")));
				if(fuzzy)
				{
					Arrays.stream(doc.getValues("originallabel")).filter(l->l.length()>3).forEach(
							l->idWithScore.put(doc.get(fieldName), (double)distance.getDistance(ns, normalize(l))));
				}
				else
				{
					if(hit.score>=Config.INSTANCE.indexMinLuceneScore)
					{
						log.trace(searcher.explain(q, hit.doc));
						Arrays.stream(doc.getValues("originallabel")).filter(l->l.length()>3)
						// even if transposed should have some minimal string distance
						.filter(l->distance.getDistance(ns, normalize(l))>0.5)
						// original label is the document from lucene which can be much longer than our string so we make sure they are not too dissimilar in length
						.filter(l->ns.length()*2>normalize(l).length())
						.forEach(
								l->idWithUnnormalizedScore.put(doc.get(fieldName), (double) hit.score));
					}
				}
				//				log.debug("label index result labels "+Arrays.toString(doc.getValues("originallabel"))+", uri "+doc.get("uri")+" score "+score);
			}
			if(hits.length>0)
			{
				if(!fuzzy)
				{
					double max = idWithUnnormalizedScore.values().stream().reduce(0.0, Double::max);
					idWithUnnormalizedScore.forEach((ss,l)->{idWithScore.put(ss,l/max);});
				}
				break; // only use second index when fuzzy one doesn't work
			}
		}
		// only keep elements with a score of at least minScore
		return idWithScore.keySet().stream().filter(id->idWithScore.get(id)>=minScore).collect(Collectors.toMap(id->id, id->idWithScore.get(id)));
	}
}