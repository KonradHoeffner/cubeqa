package org.aksw.autosparql.cube;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.aksw.autosparql.cube.property.ComponentProperty;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.RDFNode;

/** Represents an RDF Data Cube with its component properties */
@RequiredArgsConstructor
@ToString
public class Cube implements Serializable
{
	private static final long	serialVersionUID	= 1L;

	public final String name;
	public final String uri;
	//	public final Set<String> labels = new TreeSet<String>();
	public final Map<String,ComponentProperty> properties;

	public final CubeSparql sparql = CubeSparql.LINKED_SPENDING;

	static Map<String,Cube> instances = new HashMap<>();

	static boolean USE_CACHE = true;
	static private File cacheFolder = new File("cache");
	static {cacheFolder.mkdir();}

	static String extractName(RDFNode node)
	{
		String uri = node.asResource().getURI();
		return uri.substring(uri.lastIndexOf("/")+1);
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
			Map<String,ComponentProperty> properties = new HashMap<>();

			String query = "select distinct ?p "+// //?type ?label "+
					"from <http://linkedspending.aksw.org/"+cubeName+"> "+
					"from <http://linkedspending.aksw.org/ontology/> "+
					"{"+
					" ls:"+cubeName+" qb:structure ?dsd. ?dsd qb:component ?comp."+
					" {?comp qb:dimension ?p.} UNION {?comp qb:attribute ?p.} UNION {?comp qb:measure ?p.} "+
					//					" ?p a ?type. FILTER (?type != <"+RDF.Property.getURI()+"> && ?type != <"+DataModel.DataCube.ComponentProperty.getURI()+">)"+
					//					" OPTIONAL {?p rdfs:label ?label}"+
					"}";
			//			System.out.println(query);
			ResultSet rs = CubeSparql.LINKED_SPENDING.select(query);
			String uri = "http://linkedspending.aksw.org/instance/"+cubeName;
			c = new Cube(cubeName,uri, properties);
			instances.put(cubeName, c);
			while(rs.hasNext())
			{
				QuerySolution qs = rs.nextSolution();

				// because of ComponentProperty's multiton pattern, having the same property multiple times is not a problem and in fact necessary for multiple labels
				String propertyUri = qs.get("p").asResource().getURI();
				if(!propertyUri.contains("recipient-country")) continue;
				ComponentProperty property = ComponentProperty.getInstance(c, propertyUri);//, qs.get("type").asResource().getURI());
				properties.put(propertyUri, property);
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

	@Override public int hashCode() {return name.hashCode();}

	@Override public boolean equals(Object obj)
	{
		if(!(obj instanceof Cube)) return false;
		return this.name.equals(((Cube)obj).name);
	}

}