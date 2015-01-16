package org.aksw.autosparql.cube.template;

import static org.aksw.autosparql.cube.Trees.phrase;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.aksw.autosparql.cube.Cube;
import org.aksw.autosparql.cube.property.ComponentProperty;
import org.aksw.autosparql.cube.property.scorer.ScoreResult;
import org.aksw.autosparql.cube.property.scorer.Scorers;
import edu.stanford.nlp.trees.Tree;

@RequiredArgsConstructor
@Log
public class CubeTemplatorNew
{
	private final Cube cube;
	//	private final String question;

	public CubeTemplate buildTemplate(String question)
	{
		Tree root = StanfordNlp.parse(question);
		return visitRecursive(root).toTemplate();
	}

	List<CubeTemplateFragment> fragments(List<Tree> trees, Predicate<CubeTemplateFragment> predicate)
	{
		return trees.stream()
				.map(this::visitRecursive)
				.filter(predicate)
				.collect(Collectors.toList());
	}

	CubeTemplateFragment visitRecursive(Tree tree)
	{
		while(/*!tree.isPreTerminal()&&*/tree.children().length==1)
		{
			// skipping down
			tree = tree.getChild(0);
		}
		String phrase = phrase(tree);
		if(phrase.length()<3)
		{
			System.out.println("phrase less than 3 characters, skipped: "+phrase);
			return new CubeTemplateFragment(cube, phrase);
		}
		System.out.println("visiting tree "+tree);
		System.out.print("Phrase \""+phrase+"\"...");
		MatchResult result = identify(phrase);
		if(result.isEmpty())
		{
			// can't match the whole phrase, match subtrees separately
			System.out.println("unmatched, looking at subtrees");
			List<CubeTemplateFragment> fragments = fragments(tree.getChildrenAsList(),x->true);
			List<CubeTemplateFragment> matchedFragments = fragments.stream().filter(f->!f.isEmpty()).collect(Collectors.toList());
			List<CubeTemplateFragment> unmatchedFragments = new LinkedList<>(fragments);
			unmatchedFragments.removeAll(matchedFragments);

			List<CubeTemplateFragment> selectedFragments = new ArrayList<>(matchedFragments);

			if(!unmatchedFragments.isEmpty())
			{
				MatchResult unmatchedResult = identify(CubeTemplateFragment.combine(unmatchedFragments).phrase);
				System.out.print("unmatched fragments with phrase \""+unmatchedResult.phrase+"\"...");
//				unmatchedFragments.stream().map(f->f.phrase).collect(Collectors.toList());
				// can we match these leftover fragments together?

				if(unmatchedResult.isEmpty())
				{
					System.out.println("unmatched");
				} else
				{
					System.out.println("matched to "+unmatchedResult);
					selectedFragments.add(unmatchedResult.toFragment(cube));
				}
			}
			if(selectedFragments.isEmpty())
			{
				System.out.println("no match found for phrase \"" +phrase+"\"");
				return new CubeTemplateFragment(cube,phrase);
			} else
			{
				return CubeTemplateFragment.combine(selectedFragments);
			}
		}
		else
		{
			// whole phrase matched, subtrees skipped
			log.info("identified for tree"+tree+":"+result);
			return result.toFragment(cube);
		}
	}

	public MatchResult identify(String phrase)
	{
		Map<ComponentProperty,Double> nameRefs = Scorers.scorePhraseProperties(cube,phrase);
		Map<ComponentProperty,ScoreResult> valueRefs = Scorers.scorePhraseValues(cube,phrase);
		return new MatchResult(phrase, nameRefs, valueRefs);
	}

}