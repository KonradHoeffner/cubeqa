package org.aksw.cubeqa.benchmark;

import java.util.HashSet;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;

/** Applies a function to all added strings.*/
@RequiredArgsConstructor
public class NormalizingStringSet extends HashSet<String>
{
	final Function<String,String> f;

	@Override public boolean add(String e)
	{
		return super.add(f.apply(e));
	}
}