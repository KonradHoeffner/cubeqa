package org.aksw.autosparql.cube.benchmark;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.util.Date;
import org.aksw.autosparql.cube.Algorithm;
import org.junit.Test;
import com.hp.hpl.jena.query.ResultSet;

public class FinlandAidTest
{

	@Test public void test() throws FileNotFoundException
	{
		Algorithm a = new Algorithm(FinlandAid.CUBE_NAME);
		//		a.answer(FinlandAid.questions.get(25));
		try(PrintWriter out = new PrintWriter("finland-aid-"+new Date()))
		{
			out.println("<html><body><table>");
			out.println("<tr><th>nr</th><th>query</th><th> has results</th></tr>");
			int nr = 0;
			for(String question: FinlandAid.questions)
			{
				String query = a.answer(question);
				String results = "no results";
				try
				{
					ResultSet rs = a.cube.sparql.select(query);
					if(rs.hasNext()) {results="has results";}
				}
				catch(Exception e) {results = "exception";}
				String sparqlLink = "http://linkedspending.aksw.org/sparql?query="+URLEncoder.encode(query);
				out.println("<tr><td>"+(++nr)+"</td><td><a href='"+query+"'>"+sparqlLink+"</a></td><td>"+results+"</td>");
				out.flush();
			}
			out.println("<table></body></html>");
		}
	}

}
