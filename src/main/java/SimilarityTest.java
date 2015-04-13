import org.apache.lucene.search.spell.*;

public class SimilarityTest
{
	public static void main(String[] args)
	{
		String original = "amounts extended";
		String transposed = "extended amounts";
		StringDistance[] distances = {new NGramDistance(),new JaroWinklerDistance(),new LevensteinDistance()};

		for(StringDistance dist: distances) System.out.println(String.format("%18s",dist)+" "+dist.getDistance(original, transposed));
	}
}