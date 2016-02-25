package org.aksw.cubeqa;

import java.util.HashMap;
import java.util.Map;
import org.aksw.commons.util.StopWatch;
import org.apache.commons.collections15.MultiMap;
import org.apache.commons.collections15.multimap.MultiHashMap;
import lombok.ToString;

@ToString
public enum  StopWatches
{
	INSTANCE;
	
	MultiMap<String, StopWatch> watches = new MultiHashMap<>();

	public StopWatch getWatch(String category)
	{
		StopWatch watch = new StopWatch();
		return watches.put(category, watch);
	}

	public Map<String,Long> elapsedTimesMs()
	{
		Map<String,Long> times = new HashMap<>();
		watches.keySet().stream().forEachOrdered(c->times.put(c, watches.get(c).stream().mapToLong(StopWatch::getElapsedTime).sum()));
		return times;
	}
}