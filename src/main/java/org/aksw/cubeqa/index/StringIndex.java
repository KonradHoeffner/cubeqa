package org.aksw.cubeqa.index;

import java.io.IOException;
import java.util.*;
import java.util.stream.Stream;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j;
import org.aksw.cubeqa.Config;
import org.aksw.cubeqa.property.ComponentProperty;
import org.apache.lucene.document.*;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.*;

/** Index for String scorer. */
@Log4j
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
		return getIdWithScore(s, "originallabel",Config.INSTANCE.indexMinScore);
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