package org.aksw.cubeqa;

import java.util.*;
import java.io.*;
import org.aksw.cubeqa.property.ComponentProperty;
import org.aksw.cubeqa.property.scorer.NopScorer;
import org.apache.commons.collections15.MultiMap;
import org.apache.commons.collections15.multimap.MultiHashMap;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.RDFNode;
import de.konradhoeffner.commons.TSVReader;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.log4j.Log4j;

/** Represents an RDF Data Cube with its component properties */
@RequiredArgsConstructor
@ToString(of="uri")
@Log4j
public class Cube implements Serializable
{
	private static final long	serialVersionUID	= 1L;

	public final String name;
	public final String uri;
	public final String label;
	public final String comment;

	//	public final Set<String> labels = new TreeSet<String>();
	public final Map<String,ComponentProperty> properties;

	public final CubeSparql sparql;

	static Map<String,Cube> instances = new HashMap<>();
	/** manually created additional labels in case the original labels are not good enough*/
	public final MultiMap<String,String> manualLabels;

	static boolean USE_CACHE = false;
	static private File cacheFolder = new File("cache");
	static {cacheFolder.mkdir();}

	static public Cube finlandAid()
	{
		//		if(1==1) {throw new IllegalAccessError();}
		return Cube.getInstance("finland-aid");
	}

	static String extractName(RDFNode node)
	{
		String uri = node.asResource().getURI();
		return uri.substring(uri.lastIndexOf("/")+1);
	}

	static public String linkedSpendingUri(String name) {return "http://linkedspending.aksw.org/instance/"+name;}
	static public String linkedSpendingCubeName(String uri) {return uri.replaceAll("http://linkedspending.aksw.org/instance/", "");}

	public String probablyUniqueAsciiId()
	{
		return uri.replaceAll("[^A-Za-z0-9]", "");
	}

	private static File cubeFile(String cubeName) {return new File(cacheFolder, cubeName+".ser");}

	private static synchronized Optional<Cube> loadCube(String cubeName)
	{
		File f = cubeFile(cubeName);
		try(ObjectInputStream in = new ObjectInputStream(new FileInputStream(f)))
		{
			return Optional.of((Cube)in.readObject());
		}
		catch(InvalidClassException e) {f.delete();return Optional.empty();}
		catch (FileNotFoundException e) {return Optional.empty();}
		catch (ClassNotFoundException | IOException e) {throw new RuntimeException(e);}
	}

	private void save()
	{
		System.setProperty("sun.io.serialization.extendedDebugInfo", "true");
		synchronized(Cube.class)
		{
			try(ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(cubeFile(this.name))))
			{
				out.writeObject(this);
			}
			catch (IOException e) {throw new RuntimeException(e);}
		}
	}

	/**	Finds a subset of candidate cubes that a question could be meant for. */
	public static Set<Cube> findCube(String question,Set<Cube> cubes)
	{
		HashSet<Cube> candidates = new HashSet<>();
		for(Cube cube: cubes)
		{

		}

		return candidates;
	}

	public static synchronized Cube getInstance(String cubeName)
	{
		Cube c = instances.get(cubeName);
		if(c==null)
		{
			if(USE_CACHE)
			{
				Optional<Cube> loadedCube = loadCube(cubeName);
				if(loadedCube.isPresent())
				{
					c = loadedCube.get();
					instances.put(cubeName, c);
					return c;
				};
			}
			String uri = linkedSpendingUri(cubeName);

			Map<String,ComponentProperty> properties = new HashMap<>();

			String cubeLabel = "";
			String cubeComment = "";
			String labelCommentQuery = "select * {<"+uri+"> rdfs:label ?label. <"+uri+"> rdfs:comment ?comment.}";
			try
			{
				QuerySolution qsLcq = CubeSparql.getLinkedSpendingInstanceForName(cubeName).select(labelCommentQuery).nextSolution();
				cubeLabel = qsLcq.contains("label")?qsLcq.get("label").asLiteral().getLexicalForm():"";
				cubeComment = qsLcq.contains("comment")?qsLcq.get("comment").asLiteral().getLexicalForm():"";
			} catch(NoSuchElementException e) {throw new RuntimeException("Error with query: "+labelCommentQuery,e);}

			String query = "select distinct ?p "+// //?type ?label "+
					"from <http://linkedspending.aksw.org/"+cubeName+"> "+
					"from <http://linkedspending.aksw.org/ontology/> "+
					"{"+
					" ls:"+cubeName+" qb:structure ?dsd. ?dsd qb:component ?comp."+
					" {?comp qb:dimension ?p.} UNION {?comp qb:attribute ?p.} UNION {?comp qb:measure ?p.} "+
					//					" ?p a ?type. FILTER (?type != <"+RDF.Property.getURI()+"> && ?type != <"+DataModel.DataCube.ComponentProperty.getURI()+">)"+
					//					" OPTIONAL {?p rdfs:label ?label}"+
					"}";
			ResultSet rs = CubeSparql.getLinkedSpendingInstanceForName(cubeName).select(query);

			MultiMap<String,String> manualLabels = new MultiHashMap<>();

			InputStream labelStream = Cube.class.getClassLoader().getResourceAsStream(cubeName+"/manuallabels.tsv");
			//			if(labelStream==null) throw new RuntimeException("manual labels not found");// for testing
			if(labelStream!=null)
			{
				try(TSVReader reader = new TSVReader(labelStream))
				{
					while(reader.hasNextTokens())
					{
						String[] tokens = reader.nextTokens();
						Arrays.stream(tokens, 1, tokens.length).forEach(label->manualLabels.put(tokens[0], label));
					}
				}
				catch (IOException e) {throw new RuntimeException("Exception reading additional labels from tsv file.",e);}
			}
			// TODO: make the multi map unmodifiable
			c = new Cube(cubeName,uri,cubeLabel,cubeComment, properties,CubeSparql.getLinkedSpendingInstanceForUri(uri),manualLabels);
			instances.put(cubeName, c);
			while(rs.hasNext())
			{
				QuerySolution qs = rs.nextSolution();

				// because of ComponentProperty's multiton pattern, having the same property multiple times is not a problem and in fact necessary for multiple labels
				String propertyUri = qs.get("p").asResource().getURI();

				ComponentProperty property = ComponentProperty.getInstance(c, propertyUri);//, qs.get("type").asResource().getURI());
				if(property.scorer!=NopScorer.INSTANCE)
				{
					// only properties with scorer are useful for us
					properties.put(propertyUri, property);
				}
				//				if(qs.contains("label")) {property.labels.add(qs.get("label").asLiteral().getLexicalForm());}
			}
		}
		c.save();
		return c;
	}

	//	static Set<Cube> fromEndpoint(String endpointUrl, String cubeName)
	//	{
	//		Set<Cube> cubes = new HashSet<>();
	//		String query = "select ?qb ?id {?qb a qb:DataSet. }";
	//		return cubes;
	//	}

	@Override public int hashCode() {return uri.hashCode();}

	@Override public boolean equals(Object obj)
	{
		if(!(obj instanceof Cube)) {
			return false;
		}
		return this.uri.equals(((Cube)obj).uri);
	}

	public ComponentProperty getDefaultAnswerProperty()
	{
		return ComponentProperty.getInstance(this,"http://linkedspending.aksw.org/ontology/"+name+"-amount");
	}
}