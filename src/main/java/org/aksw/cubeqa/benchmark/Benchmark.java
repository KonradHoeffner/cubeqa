package org.aksw.cubeqa.benchmark;

import static de.konradhoeffner.commons.Streams.stream;
import java.io.*;
import java.nio.charset.Charset;
import java.util.*;
import java.util.stream.Collectors;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.SchemaFactory;
import lombok.*;
import org.aksw.cubeqa.Algorithm;
import org.aksw.cubeqa.CubeSparql;
import org.apache.commons.csv.*;
import org.w3c.dom.*;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.RDFNode;
import de.konradhoeffner.commons.Pair;

/** Abstract benchmark class with evaluate function.*/
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class Benchmark
{
	private final String name;
	private final List<Question> questions;

	/** CSV does not contain answers. file gets loaded from benchmark/name.csv. */
	public static Benchmark fromCsv(String name) throws IOException
	{
		List<Question> questions = new LinkedList<Question>();
		try(CSVParser parser = CSVParser.parse(new File(new File("benchmark"),name+".csv"),Charset.defaultCharset(),CSVFormat.DEFAULT))
		{
			for(CSVRecord record: parser)
			{
				questions.add(new Question(record.get(0),record.get(1),Collections.emptySet()));
			}
		}
		return new Benchmark(name,questions);
	}

	String nodeString(RDFNode node)
	{
		if(node.isLiteral()) return node.asLiteral().getLexicalForm();
		if(node.isResource()) return node.asResource().getURI();
		throw new IllegalArgumentException();
	}

	/** QALD XML format with answers. file gets loaded from benchmark/name.xml. */
	@SneakyThrows
	public static Benchmark fromQald(String name)
	{
		File file= new File("benchmark/"+name+".xml");
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		// only works with xsd not dtd?
		//		dbFactory.setValidating(true);
		//		SchemaFactory schemaFactory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
		//			factory.setSchema(schemaFactory.newSchema(
		//			    new Source[] {new StreamSource("benchmark/qaldcube.dtd")}));
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(file);
		doc.getDocumentElement().normalize();
		NodeList questionNodes = doc.getElementsByTagName("question");

		for(int i=0; i<questionNodes.getLength();i++)
		{
			Set<Map<String,Object>> answers = new HashSet<>();
			Element questionElement = (Element) questionNodes.item(i);
			String string = questionElement.getElementsByTagName("string").item(0).getTextContent();
			String query = questionElement.getElementsByTagName("query").item(0).getTextContent();
			Element answersElement = (Element) questionElement.getElementsByTagName("answers").item(0);
			NodeList answerElements = answersElement.getChildNodes();
			for(int j=0;j<answerElements.getLength();j++)
			{
				Map<String,Object> answer = new HashMap<>();
				Element answerElement = (Element) answerElements.item(j);
				NodeList childNodes = answerElement.getChildNodes();
				if(childNodes.getLength()==0)
				{
					answer.put("result", answerElement.getTextContent());
				} else
				{
					for(int k=0;k<childNodes.getLength();k++)
					{
						Element cell = (Element)childNodes.item(k);
						answer.put(cell.getNodeName(), cell.getTextContent());
					}
				}
				answers.add(answer);
			}
			Question question = new Question(string, query, answers);
		}


		for (int temp = 0; temp < questionNodes.getLength(); temp++) {

			Node nNode = questionNodes.item(temp);
		}
		return null;
	}

	@SneakyThrows
	/** file gets saved to benchmark/name.xml */
	public void saveAsQald(CubeSparql sparql) throws IOException
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
				writer.writeAttribute("hybrid","false");
				writer.writeAttribute("statistical","true");
				writer.writeCharacters("\n");
				writer.writeStartElement("string");
				writer.writeCharacters(question.string);
				writer.writeEndElement();
				writer.writeCharacters("\n");
				writer.writeStartElement("query");
				writer.writeCharacters("\n");
				writer.writeCharacters(question.query);
				writer.writeStartElement("answers");
				writer.writeCharacters("\n");
				if(question.query.startsWith("ask"))
				{
					writer.writeStartElement("answer");
					writer.writeAttribute("answerType","boolean");
					writer.writeCharacters(String.valueOf(sparql.ask(question.query)));
					writer.writeEndElement();

				} else if(question.query.startsWith("select"))
				{
					ResultSet rs = sparql.select(question.query);
					List<String> varNames = null;
					while(rs.hasNext())
					{
						writer.writeStartElement("answer");
						QuerySolution qs = rs.nextSolution();
						//						if(varNames==null) // unions may have empty parts so recalculate
						{varNames = stream(qs.varNames()).collect(Collectors.toList());}
						if(varNames.size()==1)
						{
							writer.writeAttribute("answerType",AnswerType.typeOf(qs.get(varNames.get(0))).toString().toLowerCase());
							writer.writeCharacters(nodeString(qs.get(varNames.get(0))));
						} else
						{
							for(String var: varNames)
							{
								writer.writeStartElement(var);
								writer.writeAttribute("answerType",AnswerType.typeOf(qs.get(var)).toString().toLowerCase());
								writer.writeCharacters(nodeString(qs.get(var)));
								writer.writeEndElement();
							}
						}
						writer.writeEndElement();
						writer.writeCharacters("\n");
					}
				} else throw new IllegalArgumentException("unsupported SPARQL query type (neither ASK nor SELECT): "+question.query);
				writer.writeEndElement();
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
			//				 Set<String> answers = algorithm.cube.sparql.select(query);

		}
		System.out.println(precisionRecalls.stream().mapToDouble(Pair::getA).filter(d->d==1).count()+" with precision 1");
		System.out.println(precisionRecalls.stream().mapToDouble(Pair::getB).filter(d->d==1).count()+" with recall 1");
		System.out.println(precisionRecalls.stream().mapToDouble(Pair::getA).average());
		System.out.println(precisionRecalls.stream().mapToDouble(Pair::getB).average());
	}
}