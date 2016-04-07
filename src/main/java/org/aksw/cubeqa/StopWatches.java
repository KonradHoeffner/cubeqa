package org.aksw.cubeqa;

import java.util.HashMap;
import java.util.Map;
import de.konradhoeffner.commons.StopWatch;
import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.HashSetValuedHashMap;
import lombok.ToString;

@ToString
public enum  StopWatches
{
	INSTANCE;
	
	MultiValuedMap<String, StopWatch> watches = new HashSetValuedHashMap<>();

	public StopWatch getWatch(String category)
	{
		StopWatch watch = new StopWatch();
		watches.put(category, watch);
		return watch;
	}

	public Map<String,Long> elapsedTimesMs()
	{
		Map<String,Long> times = new HashMap<>();
		watches.keySet().stream().forEachOrdered(c->times.put(c, watches.get(c).stream().mapToLong(StopWatch::getElapsedTime).sum()));
		return times;
	}
}