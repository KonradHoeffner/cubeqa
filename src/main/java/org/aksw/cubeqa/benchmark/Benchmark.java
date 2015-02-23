package org.aksw.cubeqa.benchmark;

import java.util.ArrayList;
import java.util.List;
import org.aksw.cubeqa.Algorithm;
import org.aksw.cubeqa.template.CubeTemplate;
import de.konradhoeffner.commons.Pair;

/** Abstract benchmark class with evaluate function.*/
public abstract class Benchmark
{
	 abstract List<BenchmarkElement> getBenchmark();
	 abstract Algorithm getAlgorithm();

	 void evaluate()
	 {
//		System.out.println(getBenchmark().size());
		System.out.println(getBenchmark().stream()/*.filter(be->be.doable)*/.count());
		int count = 0;
		List<Pair<Double,Double>> precisionRecalls = new ArrayList<>();
		 for(BenchmarkElement be: getBenchmark())
		 {
			 System.out.println(++count+" Answering "+be.question);
			 CubeTemplate correct = be.template;
			 CubeTemplate candidate = getAlgorithm().answer(be.question);
			 Pair<Double,Double> precRec = CubeTemplate.precisionRecallDimensions(correct, candidate);
			 if(precRec!=null)
			 {
			 System.out.println(precRec);
			 precisionRecalls.add(precRec);
			 } else
			 {
			  System.err.println("prec rec null");
			 }
			 break;
		 }
		 System.out.println(precisionRecalls.stream().mapToDouble(Pair::getA).filter(d->d==1).count()+" with precision 1");
		 System.out.println(precisionRecalls.stream().mapToDouble(Pair::getB).filter(d->d==1).count()+" with recall 1");
		 System.out.println(precisionRecalls.stream().mapToDouble(Pair::getA).average());
		 System.out.println(precisionRecalls.stream().mapToDouble(Pair::getB).average());
	 }
}