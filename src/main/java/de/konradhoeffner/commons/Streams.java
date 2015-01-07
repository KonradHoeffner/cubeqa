package de.konradhoeffner.commons;

import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.concurrent.Callable;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class Streams
{
	static public <T> Stream<T> stream(Iterator<T> it)
	{
		Stream<T> targetStream = StreamSupport.stream(
		          Spliterators.spliteratorUnknownSize(it, Spliterator.ORDERED),false);
		return targetStream;
	}

    public static <V> V propagate(Callable<V> callable){
        try {
            return callable.call();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}