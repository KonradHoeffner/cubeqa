package org.aksw.cubeqa.property.scorer;

import java.util.Optional;

/** No Operation Scorer used when no other Scorer could be identified.*/
public final class NopScorer extends Scorer
{
	private NopScorer() {super(null);}

	public final static NopScorer INSTANCE = new NopScorer();

	@Override public Optional<ScoreResult> score(String value) {return Optional.empty();}
}