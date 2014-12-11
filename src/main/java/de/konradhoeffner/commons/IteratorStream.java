package de.konradhoeffner.commons;

import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class IteratorStream
{
	static public <T> Stream<T> stream(Iterator<T> it)
	{
		Stream<T> targetStream = StreamSupport.stream(
		          Spliterators.spliteratorUnknownSize(it, Spliterator.ORDERED),false);
		return targetStream;
	}
}