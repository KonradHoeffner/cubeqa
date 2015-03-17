package org.aksw.cubeqa.benchmark;

import java.io.*;
import java.nio.charset.Charset;
import java.util.LinkedList;
import java.util.List;
import javax.xml.stream.*;
import lombok.*;
import org.apache.commons.csv.*;
import edu.northwestern.at.utils.xml.IndentingXMLWriter;

/** Abstract benchmark class with evaluate function.*/
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class Benchmark
{
	private final List<Question> questions;

	public static Benchmark fromCsv(File file) throws IOException
	{
		List<Question> questions = new LinkedList<Question>();
		try(CSVParser parser = CSVParser.parse(file,Charset.defaultCharset(),CSVFormat.DEFAULT))
		{
			for(CSVRecord record: parser)
			{
				questions.add(new Question(record.get(0),record.get(1)));
			}
		}
		return new Benchmark(questions);
	}

	@SneakyThrows
	public void saveAsQald(File file, String datasetId) throws IOException
	{
		int id = 0;
		try(FileWriter fw = new FileWriter(file))
		{
			XMLStreamWriter writer = XMLOutputFactory.newInstance().createXMLStreamWriter(fw);
			writer.writeStartDocument();
			writer.writeStartElement("dataset");
			writer.writeAttribute("id",datasetId);
			for(Question question: questions)
			{
				writer.writeStartElement("question");
				writer.writeAttribute("id",String.valueOf(++id));
				writer.writeCharacters("\n");
				writer.writeStartElement("string");
				writer.writeCharacters(question.string);
				writer.writeEndElement();
				writer.writeCharacters("\n");
				writer.writeStartElement("query");
				writer.writeCharacters("\n");
				writer.writeCharacters(question.query);
				writer.writeCharacters("\n");
				writer.writeEndElement();
				writer.writeCharacters("\n");
				writer.writeEndElement();
				writer.writeCharacters("\n");
			}
			writer.writeEndElement();
			writer.writeEndDocument();
			writer.close();
		}
	}


	//	 void evaluate(Algorithm algorithm)
	//	 {
	////		System.out.println(getBenchmark().size());
	//		System.out.println(questions.stream().count());
	//		int count = 0;
	//		List<Pair<Double,Double>> precisionRecalls = new ArrayList<>();
	//		 for(Question question: questions)
	//		 {
	//			 System.out.println(++count+" Answering "+question.string);
	//			 CubeTemplate correct = question.template;
	//			 CubeTemplate candidate = getAlgorithm().answer(question.question);
	//			 Pair<Double,Double> precRec = CubeTemplate.precisionRecallDimensions(correct, candidate);
	//			 if(precRec!=null)
	//			 {
	//			 System.out.println(precRec);
	//			 precisionRecalls.add(precRec);
	//			 } else
	//			 {
	//			  System.err.println("prec rec null");
	//			 }
	//			 break;
	//		 }
	//		 System.out.println(precisionRecalls.stream().mapToDouble(Pair::getA).filter(d->d==1).count()+" with precision 1");
	//		 System.out.println(precisionRecalls.stream().mapToDouble(Pair::getB).filter(d->d==1).count()+" with recall 1");
	//		 System.out.println(precisionRecalls.stream().mapToDouble(Pair::getA).average());
	//		 System.out.println(precisionRecalls.stream().mapToDouble(Pair::getB).average());
	//	 }
}