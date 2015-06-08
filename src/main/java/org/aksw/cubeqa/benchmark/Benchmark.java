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
import lombok.*;
import lombok.extern.log4j.Log4j;
import org.aksw.cubeqa.Algorithm;
import org.aksw.cubeqa.CubeSparql;
import org.apache.commons.csv.*;
import org.apache.log4j.Level;
import org.w3c.dom.*;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.RDFNode;

/** Abstract benchmark class with evaluate function.*/
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Log4j
public class Benchmark
{
	protected final String name;
	protected final List<Question> questions;

	public void completeQuestions(CubeSparql sparql)
	{
		questions.stream().forEach(q->completeQuestion(sparql, q.string, q.query));
	}

	static Question completeQuestion(CubeSparql sparql, String string, String query)
	{
		Set<Map<String,String>> answers = new HashSet<>();
		Map<String,DataType> tagTypes  = new HashMap<>();

		if(query.startsWith("ask"))
		{
			tagTypes.put("",DataType.BOOLEAN);
			Map<String,String> answer = new HashMap<String,String>();
			answer.put("",String.valueOf(sparql.ask(query)));
			answers.add(Collections.unmodifiableMap(answer));
		}
		else if(query.startsWith("select"))
		{
			ResultSet rs = sparql.select(query);
			Set<String> varNames = new TreeSet<String>();
			while(rs.hasNext())
			{
				Map answer = new HashMap<String,Object>();
				QuerySolution qs = rs.nextSolution();
				// TODO: how to deal with unions where one part does not exist?
				varNames.addAll(stream(qs.varNames()).collect(Collectors.toList()));
				if(varNames.size()==1)
				{
					RDFNode node = qs.get(varNames.iterator().next());
					tagTypes.put("",DataType.typeOf(node));
					String ns = nodeString(qs.get(varNames.iterator().next()));
					if(!ns.isEmpty()) {answer.put("", ns);}
				} else
				{
					for(String var: varNames)
					{
						RDFNode node = qs.get(var);
						if(node!=null)
						{
							tagTypes.put(var,DataType.typeOf(node));
							String ns = nodeString(qs.get(var));
							if(!ns.isEmpty()) {answer.put(var, ns);}
						}
					}
				}
				if(!answer.isEmpty()) {answers.add(Collections.unmodifiableMap(answer));}

			}
		}
		return new Question(string, query, answers, tagTypes);
	}

	public void evaluate(Algorithm algorithm)
	{
		log.info("Evaluating cube "+algorithm.cube.name+ " on benchmark "+name+" with "+questions.size()+" questions");
		List<Performance> performances = new ArrayList<>();
		int count = 0;
		int unionCount = 0;
		int subqueryCount = 0;
		int askCount = 0;
		for(int i=1;i<=questions.size();i++)
		{
			if(i==74) {performances.add(new Performance(0,0,true));continue;} // q 74 gets wrongly positively evaluated
			Question q = questions.get(i-1);
//			// remove questions with unions
			if(q.query.toLowerCase().contains("union")) {unionCount++;continue;}
//			// remove questions with subqueries
			if(q.query.toLowerCase().substring(5).contains("select")) {subqueryCount++;continue;}
//			// remove ask queries
			if(q.query.toLowerCase().startsWith("ask")) {askCount++;continue;}
			count++;
			performances.add(evaluate(algorithm,i));
		}
		log.info(count+" questions processed");
		System.out.println(unionCount+ "union queries");
		System.out.println(subqueryCount+ "sub queries");
		System.out.println(askCount+ "ask queries");
		log.info("Average precision "+ performances.stream().filter(p->!p.isEmpty()).mapToDouble(Performance::getPrecision).average());
		log.info("Average recall "+ performances.stream().mapToDouble(Performance::getRecall).average());
//		log.info("f score")
				log.info("Average f score "+ performances.stream().mapToDouble(Performance::fscore).average());
	}

	public Performance evaluate(Algorithm algorithm, int questionNumber)
	{
		//		List<Pair<Double,Double>> precisionRecalls = new ArrayList<>();
		log.setLevel(Level.ALL);
		Question question = questions.get(questionNumber-1);
		log.info("Question Number "+questionNumber+": Answering "+question.string);
		log.debug("correct query: "+question.query);
		log.debug("correct answer: "+question.answers);

		String query = algorithm.answer(question.string).sparqlQuery();
		Question found;
		try
		{found = completeQuestion(algorithm.cube.sparql, question.string, query);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return new Performance(0, 0, true);
		}
		log.debug("found query: "+found.query);
		log.debug("found answer: "+found.answers);
		Performance p = Performance.performance(question.answers, found.answers);
		log.info("Performance "+p);
		return p;
	}

	/** CSV does not contain answers. file gets loaded from benchmark/name.csv. */
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

	static String nodeString(RDFNode node)
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
		List<Question> questions = new ArrayList<>();
		for(int i=0; i<questionNodes.getLength();i++)
		{
			Map<String,DataType> tagTypes = new HashMap<>();
			Set<Map<String,String>> answers = new HashSet<>();
			Element questionElement = (Element) questionNodes.item(i);
			String string = questionElement.getElementsByTagName("string").item(0).getTextContent().trim();
			String query = Nodes.directTextContent(questionElement.getElementsByTagName("query").item(0)).trim();

			Element answersElement = (Element) questionElement.getElementsByTagName("answers").item(0);
			List<Element> answerElements = Nodes.childElements(answersElement, "answer");
			for(Element answerElement: answerElements)
			{
				Map<String,String> answer = new HashMap<>();
				String direct = Nodes.directTextContent(answerElement).trim();
				if(direct.isEmpty())
				{
					for(Element var: Nodes.childElements(answerElement))
					{
						tagTypes.put(var.getTagName(), DataType.valueOf(var.getAttribute("answerType").toUpperCase()));
						answer.put(var.getTagName(), var.getTextContent());
					}
				} else
				{
					tagTypes.put("", DataType.valueOf(answerElement.getAttribute("answerType").toUpperCase()));
					answer.put("", direct);
				}
				answers.add(Collections.unmodifiableMap(answer));
			}
			Question question = new Question(string, query, answers,tagTypes);
			questions.add(question);
		}

		return new Benchmark(name, questions);
	}

	/** {@link #} */
	public void saveAsQald(CubeSparql sparql) {saveAsQald(sparql,new File (new File("benchmark"),name+".xml"));}

	/**	 */
	@SneakyThrows
	public void saveAsQald(CubeSparql sparql, File file)
	{
		int id = 0;
		try(FileWriter fw = new FileWriter(file))
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
				if(question.answers==null)
				{
					question = completeQuestion(sparql, question.string, question.query);
					////					if(true) throw new IllegalArgumentException("answers are null");
					//					log.warn("Benchmark contains no answers, querying SPARQL endpoint");
					//					if(question.query.startsWith("ask"))
					//					{
					//						writer.writeStartElement("answer");
					//						writer.writeAttribute("answerType","boolean");
					//						writer.writeCharacters(String.valueOf(sparql.ask(question.query)));
					//						writer.writeEndElement();
					//
					//					} else if(question.query.startsWith("select"))
					//					{
					//						ResultSet rs = sparql.select(question.query);
					//						List<String> varNames = null;
					//						while(rs.hasNext())
					//						{
					//							writer.writeStartElement("answer");
					//							QuerySolution qs = rs.nextSolution();
					//							//						if(varNames==null) // unions may have empty parts so recalculate
					//							{varNames = stream(qs.varNames()).collect(Collectors.toList());}
					//							if(varNames.size()==1)
					//							{
					//								writer.writeAttribute("answerType",AnswerType.typeOf(qs.get(varNames.get(0))).toString().toLowerCase());
					//								writer.writeCharacters(nodeString(qs.get(varNames.get(0))));
					//							} else
					//							{
					//								for(String var: varNames)
					//								{
					//									writer.writeStartElement(var);
					//									writer.writeAttribute("answerType",AnswerType.typeOf(qs.get(var)).toString().toLowerCase());
					//									writer.writeCharacters(nodeString(qs.get(var)));
					//									writer.writeEndElement();
					//								}
					//							}
					//							writer.writeEndElement();
					//							writer.writeCharacters("\n");
					//						}
					//					} else throw new IllegalArgumentException("unsupported SPARQL query type (neither ASK nor SELECT): "+question.query);
					//				} else
				}
				{
					for(Map<String,String> answer: question.answers)
					{
						writer.writeStartElement("answer");
						if(answer.containsKey(""))
						{
							writer.writeAttribute("answerType",question.answerTypes.get("").toString().toLowerCase());
							writer.writeCharacters(answer.get("").toString());

						} else
						{
							for(String tag: answer.keySet())
							{
								writer.writeStartElement(tag);
								writer.writeAttribute("answerType",question.answerTypes.get(tag).toString().toLowerCase());
								writer.writeCharacters(answer.get(tag).toString());
								writer.writeEndElement();
								writer.writeCharacters("\n");
							}
						}
						writer.writeEndElement();
						writer.writeCharacters("\n");
					}
				}
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

}