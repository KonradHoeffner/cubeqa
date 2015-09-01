package org.aksw.cubeqa.property.scorer;

import java.util.Optional;
import org.aksw.cubeqa.property.ComponentProperty;

/** When no real scorer could be found this shuts a property out from being detected.*/
public final class NopScorer extends Scorer
{
	private NopScorer() {super(null);}

	public final static NopScorer INSTANCE = new NopScorer();

	@Override public Optional<ScoreResult> score(String value) {return Optional.empty();}
}