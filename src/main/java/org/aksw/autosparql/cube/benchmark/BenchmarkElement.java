package org.aksw.autosparql.cube.benchmark;

import lombok.RequiredArgsConstructor;
import org.aksw.autosparql.cube.template.CubeTemplate;

@RequiredArgsConstructor
class BenchmarkElement {
	public final String question;
	public final boolean doable;
	public final CubeTemplate template;
}