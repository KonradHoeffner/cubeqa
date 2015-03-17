package org.aksw.cubeqa.benchmark;

import java.io.*;
import java.nio.charset.Charset;
import java.util.*;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;
import lombok.*;
import org.aksw.cubeqa.Algorithm;
import org.aksw.cubeqa.template.CubeTemplate;
import org.apache.commons.csv.*;
import de.konradhoeffner.commons.Pair;

/** Abstract benchmark class with evaluate function.*/
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class Benchmark
{
	private final String name;
	private final List<Question> questions;

	/** file gets loaded from benchmark/name.csv */
	public static Benchmark fromCsv(String name) throws IOException
	{
		List<Question> questions = new LinkedList<Question>();
		try(CSVParser parser = CSVParser.parse(new File(new File("benchmark"),name+".csv"),Charset.defaultCharset(),CSVFormat.DEFAULT))
		{
			for(CSVRecord record: parser)
			{
				questions.add(new Question(record.get(0),record.get(1)));
			}
		}
		return new Benchmark(name,questions);
	}

	@SneakyThrows
	/** file gets saved to benchmark/name.xml */
	public void saveAsQald() throws IOException
	{
		int id = 0;
		try(FileWriter fw = new FileWriter(new File(new File("benchmark"),name+".xml")))
		{
			XMLStreamWriter writer = XMLOutputFactory.newInstance().createXMLStreamWriter(fw);
			writer.writeStartDocument();
			writer.writeStartElement("dataset");
			writer.writeAttribute("id",name);
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

		 void evaluate(Algorithm algorithm)
		 {
			System.out.println("Evaluating cube "+algorithm.cube.name+ " on benchmark "+name+" with "+questions.size()+" questions");
			int count = 0;
			List<Pair<Double,Double>> precisionRecalls = new ArrayList<>();
			 for(Question question: questions)
			 {
				 System.out.println(++count+" Answering "+question.string);

				 String query = algorithm.answer(question.string).sparqlQuery();
				 algorithm.cube.sparql.select(query);

			 }
			 System.out.println(precisionRecalls.stream().mapToDouble(Pair::getA).filter(d->d==1).count()+" with precision 1");
			 System.out.println(precisionRecalls.stream().mapToDouble(Pair::getB).filter(d->d==1).count()+" with recall 1");
			 System.out.println(precisionRecalls.stream().mapToDouble(Pair::getA).average());
			 System.out.println(precisionRecalls.stream().mapToDouble(Pair::getB).average());
		 }
}