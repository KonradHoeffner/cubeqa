package org.aksw.cubeqa.template;

import de.konradhoeffner.commons.Pair;
import de.konradhoeffner.commons.StopWatch;
import edu.stanford.nlp.trees.Tree;
import lombok.extern.slf4j.Slf4j;
import org.aksw.cubeqa.*;
import org.aksw.cubeqa.detector.Detector;
import org.aksw.cubeqa.property.ComponentProperty;
import org.aksw.cubeqa.property.scorer.ScoreResult;
import org.aksw.cubeqa.property.scorer.Scorers;
import java.util.*;

/** Generates the Cube Template. */
@Slf4j
public abstract class Templator
{
	//	{log.setLevel(Level.ALL);}
	protected static final int	PHRASE_MIN_LENGTH	= 3;
	protected static final int	PHRASE_MAX_LENGTH	= 30;

	protected final Cube cube;
	protected Fragment detectFragment;
	protected Tree root;
	protected EnumSet<AnswerType> eats;

	public Templator(Cube cube) {this.cube=cube;}

	protected void preprocess(String question)
 {
	 String replaced = Replacer.replace(question);
	 if(!replaced.equals(question))
	 {
		 question=replaced;
		 log.info("Replacement: "+question);
	 }
	 StopWatch eatWatch = StopWatches.INSTANCE.getWatch("eat");
	 eatWatch.start();
	 Optional<Pair<String,EnumSet<AnswerType>>> oPair = AnswerType.eatAndQuestionWord(question);
	 eatWatch.stop();
	 eats = EnumSet.allOf(AnswerType.class);
	 if(!oPair.isPresent()) {log.warn("no question word found for question '"+question+"': no answer type restriction possible.");}
	 else
	 {
		 String questionWord = oPair.get().a;
		 eats = oPair.get().b;
		 question = question.substring(questionWord.length());
	 }

	 String noStop = question;
	 if(Config.INSTANCE.removeStopWords)
	 {
		 //			noStop = Stopwords.remove(noStop, Stopwords.FINLAND_AID_WORDS);
		 noStop = Stopwords.remove(noStop, Stopwords.PROPERTY_WORDS);
	 }
	 if(!question.equals(noStop)) {log.info("removed stop words, result: "+noStop);}
	 StopWatch detectWatch = StopWatches.INSTANCE.getWatch("detect");
	 detectWatch.start();
	 Pair<Fragment,String> detectResult = detect(noStop);
	 detectWatch.stop();
	 StopWatch parseWatch = StopWatches.INSTANCE.getWatch("parse");
	 parseWatch.start();
	 root = StanfordNlp.parse(detectResult.b);
	 parseWatch.stop();
	 detectFragment=detectResult.a;
 }

	abstract public Template buildTemplate(String question);

	/** @param question the full question used on all detectors
	 * @return the combined detected fragment and the leftover phrase
	 */
	Pair<Fragment,String> detect(final String question)
	{
		Fragment allDetectorFragment = null;

		String reducedPhrase = question;
		for(Detector detector: Detector.DETECTORS)
		{
			Set<Fragment> detectorResults = detector.detect(cube,reducedPhrase);
			if(!detectorResults.isEmpty())
			{
				for(Fragment fragment: detectorResults)
				{
					reducedPhrase = question.replace(fragment.phrase,"").replace("  ", " ");
					if(reducedPhrase.equals(question)) {
						throw new IllegalArgumentException("fragment phrase '"+fragment.phrase+"' not found in whole phrase "+question);
					}
					log.debug("Detector "+detector.getClass().getSimpleName()+" matched part: '"+fragment.phrase+"', left over phrase: "+reducedPhrase);
				}
				// keep results from earlier used detectors
				if(allDetectorFragment!=null) {detectorResults.add(allDetectorFragment);}
				allDetectorFragment = Fragment.combine(new ArrayList<>(detectorResults));
			}
		}
		if(allDetectorFragment==null) {return new Pair<>(new Fragment(cube,""),reducedPhrase);}
		return new Pair<>(allDetectorFragment,reducedPhrase);
	}


	public Match identify(String phrase/*, int phraseIndex*/)
	{
		StopWatch scoreWatch = StopWatches.INSTANCE.getWatch("score");
		scoreWatch.start();
		Map<ComponentProperty,Double> nameRefs = Scorers.scorePhraseProperties(cube,phrase);
		Map<ComponentProperty,ScoreResult> valueRefs = Scorers.scorePhraseValues(cube,phrase);
		scoreWatch.stop();
		return new Match(phrase,/* phraseIndex, */nameRefs, valueRefs);
	}

}