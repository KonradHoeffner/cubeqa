package org.aksw.cubeqa.scripts;

import java.util.List;
import java.util.Scanner;
import java.io.IOException;
import org.aksw.cubeqa.Algorithm;
import org.aksw.cubeqa.Cube;
import org.aksw.cubeqa.index.CubeIndex;
import org.apache.jena.query.ResultSetFormatter;
import edu.stanford.nlp.io.StringOutputStream;

/** Web service that answers questions as strings with the W3C SPARQL Query Results XML Format. 
 * See {@link https://www.w3.org/TR/rdf-sparql-XMLres}. */
public class Service
{
	public static String answerJson(String question) throws IOException
	{
		List<String> uris = CubeIndex.INSTANCE.getCubeUris(question);
		if(uris.isEmpty()) {return "";}

		String cubeName = Cube.linkedSpendingCubeName(uris.get(0));
		try(StringOutputStream out = new StringOutputStream())
		{
			ResultSetFormatter.outputAsJSON(out,
					Cube.getInstance(cubeName).sparql.select(
							new Algorithm().template(cubeName, question).sparqlQuery()
							));
			return out.toString();
		}
	}

	public static void main(String[] args) throws IOException
	{
		System.out.println("Command line version of CubeQA.");
		String question;
		try(Scanner in = new Scanner(System.in))
		{
			do
			{
				System.out.println("Please enter a question (ENTER for exit).");
				question = in.nextLine();
				System.out.println(answerJson(question));
			} while(!question.isEmpty());
		}
	}
}