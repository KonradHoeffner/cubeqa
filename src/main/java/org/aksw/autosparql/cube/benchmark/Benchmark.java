package org.aksw.autosparql.cube.benchmark;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.aksw.autosparql.cube.Algorithm;
import org.aksw.autosparql.cube.template.CubeTemplate;
import de.konradhoeffner.commons.Pair;

public abstract class Benchmark
{
	 abstract List<BenchmarkElement> getBenchmark();
	 abstract Algorithm getAlgorithm();

	 void evaluate()
	 {
		System.out.println(getBenchmark().size());
		System.out.println(getBenchmark().stream()/*.filter(be->be.doable)*/.count());
		int count = 0;
		List<Pair<Double,Double>> precisionRecalls = new ArrayList<>();
		 for(BenchmarkElement be: getBenchmark())
		 {
			 System.out.println(++count+" Answering "+be.question);
			 CubeTemplate standard = be.template;
			 CubeTemplate candidate = getAlgorithm().answer(be.question);
			 Pair<Double,Double> precRec = CubeTemplate.precisionRecallDimensions(standard, candidate);
			 if(precRec!=null)
			 {
			 System.out.println(precRec);
			 precisionRecalls.add(precRec);
			 } else
			 {
			  System.err.println("prec rec null");
			 }
		 }
		 System.out.println(precisionRecalls.stream().mapToDouble(Pair::getA).filter(d->d==1).count()+" with precision 1");
		 System.out.println(precisionRecalls.stream().mapToDouble(Pair::getB).filter(d->d==1).count()+" with recall 1");
		 System.out.println(precisionRecalls.stream().mapToDouble(Pair::getA).average());
		 System.out.println(precisionRecalls.stream().mapToDouble(Pair::getB).average());
	 }
}
