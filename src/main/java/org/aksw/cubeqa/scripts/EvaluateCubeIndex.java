package org.aksw.cubeqa.scripts;

import java.util.ArrayList;
import java.util.List;
import org.aksw.cubeqa.benchmark.Benchmark;
import org.aksw.cubeqa.benchmark.Question;
import org.aksw.cubeqa.index.CubeIndex;
import lombok.extern.log4j.Log4j;

@Log4j
public class EvaluateCubeIndex
{
	// assumes the index is already filled with the QBench2 cubes

	public static void main(String[] args)
	{
		Benchmark bench = Benchmark.fromQald("qbench2");
		List<Boolean> equal = new ArrayList<>(bench.questions.size());
		for(Question q: bench.questions)
		{
			List<String> found = CubeIndex.INSTANCE.getCubeUris(q.string);
			boolean correct = q.cubeUri.equals(found.get(0));
			if(!correct)
			{
				log.info(q.cubeUri+" "+found.get(0));
			}
			equal.add(correct);
//			log.info(q.string+"correct cube "+q.cubeUri+", found cubes: "+cubes);
		}
		System.out.println(equal.stream().filter(e->e).count());
	}

}