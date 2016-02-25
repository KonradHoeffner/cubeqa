package org.aksw.cubeqa.index;

import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.aksw.cubeqa.property.ComponentProperty;
import org.apache.lucene.document.*;
import org.apache.lucene.index.DirectoryReader;

/** Lucene index for labels, used by ObjectPropertyScorer.
 */
@Slf4j
public class LabelIndex extends Index
{
	private static final Map<String,LabelIndex> instances = new HashMap<>();
	private LabelIndex(ComponentProperty property) {super(property);}

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
	public void fill(Set<String> uris, Function<String,Set<String>> labelFunction)
	{
		if(!DirectoryReader.indexExists(dir))
		{
			startWrites();
			for(String uri: uris)
			{
				Set<String> labels = labelFunction.apply(uri);
				add(uri, labels);
			}
			stopWrites();
		}
		reader = DirectoryReader.open(dir);
	}

	@SneakyThrows
	public Map<String,Double> getUrisWithScore(String s, double minScore)
	{
		return getIdWithScore(s, "uri",minScore);
	}

	public void add(String uri, Set<String> labels) throws IOException
	{
		if(indexWriter==null) throw new IllegalStateException("indexWriter is null, call startWrites() first.");
		Document doc = new Document();
		if(uri==null)
		{
			log.error("label index add uri==null. the following labels will not be added: "+labels);
			return;
		}
		doc.add(new StringField("uri", uri, Field.Store.YES));
		//		doc.add(new TextField("cube", cube.name, Field.Store.YES));
		//		doc.add(new TextField("property", cube.name, Field.Store.YES));

		labels.forEach(l->
		{
			doc.add(new Field("stringlabel", normalize(l), StringField.TYPE_STORED));
			doc.add(new Field("textlabel", normalize(l), TextField.TYPE_STORED));
			doc.add(new Field("originallabel", l, StringField.TYPE_STORED));
		});

		indexWriter.addDocument(doc);
	}
}