package org.aksw.cubeqa.benchmark;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.aksw.cubeqa.Aggregate;
import org.aksw.cubeqa.Algorithm;
import org.aksw.cubeqa.Cube;
import org.aksw.cubeqa.property.ComponentProperty;
import org.aksw.cubeqa.restriction.IntervalRestriction;
import org.aksw.cubeqa.restriction.Restriction;
import org.aksw.cubeqa.restriction.TopRestriction;
import org.aksw.cubeqa.restriction.TopRestriction.OrderModifier;
import org.aksw.cubeqa.restriction.UriRestriction;
import org.aksw.cubeqa.restriction.ValueRestriction;
import org.aksw.cubeqa.template.CubeTemplate;

// eclipse indent broken
// most low-tech and bad style code in this project
/**Benchmark using the LinkedSpending Finland-Aid dataset. Execute on the CubeQA algorithm via FinlandAidTest. */
public class FinlandAid extends Benchmark {
	public static final Cube cube = Cube.FINLAND_AID;
	public static final String CUBE_NAME = "finland-aid";
	public static final List < String > questions = Arrays.asList(
		"What was the average aid to environment per month in year 2010?",
		"What is the average yearly aid over the Finnfund channel?",
		"How much money was invested to strengthen civil society in Yemen?",
		"How much is committed in Ethiopia?",
		"How much does Peru receive for drinking water supply a year?",
		"How many countries have extended amounts of  > 1000000, per year?",
		"What was the average aid to Egypt over the last 10 years?",
	//			"Which were the top 10 aided countries in Europe in 2011?",
	"How much money give Finnish Red Cross on Malaria Control?",
		"What is the amount of aid and the amount of commitments per country per year?",
		"How much money did the Egyptian government receive for Disaster prevention and preparedness?",
		"What's the aid of the 10 richest and 10 poorest countries?",
		"How much did Uruguay receive?",
		"How much aid is received by Zambia on a single day?",
		"How much money goes into food crop production over time?",
		"Which type of sector is receiving the most in a particular country?",
		"In South and Central Asia, how much biodiversity aid is there?",
		"How many percent of main sector aid of a country is spent on that countries administrative costs?",
		"For what is the money, invested into Sierra Leona, used for?",
		"How much money Embassy of Finland contribute to Egyptian projects?",
		"Where goes the aid?",
		"How much money receives each Asian country from Fida International?",
		"How much education aid African countries get per year?",
		"What is average aid amount per aid sector?",
		"How much money gets each country in 2008?",
		"How much extended amounts are given to Tajikistan for Rescheduling and refinancing?",
		"Which country has the highest amount of commitments?",
		"How much air receives a country?",
		"Top 10 aid receivers in America?",
		"What is the total biodiversity aid from all sectors for countries with populations greater than 10,000,000?",
		"Where is the biggest aid to environment?",
		"How much money Nepal receives for Environmental policy and administrative management?");

	static private final String LSO = "http://linkedspending.aksw.org/ontology/";
	static private final String FINPROP = LSO + "finland-aid-";

	public static Set < ComponentProperty > finpropset(String s) {
		return Collections.singleton(finprop(s));
	}

	public static ComponentProperty finprop(String s) {
		return cube.properties.get(FINPROP + s);
	}

	public static UriRestriction countryRestriction(String countryCode) {
		return new UriRestriction(finprop("recipient-country"), "https://openspending.org/finland-aid/recipient-country/" + countryCode);
	}

	public static UriRestriction sector(String sectorCode) {
		return new UriRestriction(finprop("sector"), "https://openspending.org/finland-aid/sector/" + sectorCode);

	}

	public static Set < Restriction > sectorset(String sectorCode) {
		return Collections.singleton(sector(sectorCode));
	}

	static final ComponentProperty REF_YEAR = cube.properties.get(LSO + "refYear");
	static final Set < ComponentProperty > REF_YEAR_SET = Collections.singleton(REF_YEAR);

	static < T > Set < T > set(T...os) {
		return new HashSet < > (Arrays.asList(os));
	}


	public static BenchmarkElement be(String question, boolean doable, CubeTemplate ct) {
		return new BenchmarkElement(question, doable, ct);
	}

	@Override List<BenchmarkElement> getBenchmark() {return benchmark;}
	@Override Algorithm getAlgorithm() {return new Algorithm("finland-aid");}


	public static final List < BenchmarkElement > benchmark = Arrays.asList(
	//			 "per month" not doable with templates -> take into account that this doesnt work
	be("What was the average aid to environment per month in year 2010?",
	true,
	new CubeTemplate(cube,
	Collections.singleton(new ValueRestriction(cube.properties.get(LSO + "refYear"), "2010")),
	finpropset("aid-to-environment"),
	Collections.emptySet(),
	Collections.singleton(Aggregate.AVG))),

	be("What is the average yearly aid over the Finnfund channel?", true,
	new CubeTemplate(cube,
	Collections.singleton(new ValueRestriction(finprop("channel-of-delivery-name"), "Finnfund")),
	finpropset("amount"),
	Collections.singleton(cube.properties.get(LSO + "refYear")),
	Collections.singleton(Aggregate.AVG))),

	be("How much money was invested to strengthen civil society in Yemen?", true,
	new CubeTemplate(cube,
	set(countryRestriction("ye"), new ValueRestriction(finprop("channel-of-delivery-name"), "Civil society")),
	finpropset("amount"),
	Collections.emptySet(),
	Collections.emptySet())),

	be("How much is committed in Ethiopia?",true,
	new CubeTemplate(cube,
	Collections.singleton(countryRestriction("et")),
	finpropset("commitments"),
	Collections.emptySet(),
	Collections.emptySet())),
	//
	//// hard to match because label is "Basic drinking water supply and basic sanitation"
	be("How much does Peru receive for drinking water supply a year?",true,
	new CubeTemplate(cube,
	new HashSet < > (Arrays.asList(countryRestriction("et"), new UriRestriction(finprop("sector"), "https://openspending.org/finland-aid/sector/14030"))),
	finpropset("amount"),
	Collections.singleton(cube.properties.get(LSO + "refYear")),
	Collections.emptySet())),

	// TODO: not exactly correct, needs subquerys
	be("How many countries have extended amounts of > 1000000 $ per year?",false,
	new CubeTemplate(cube,
	Collections.singleton(new IntervalRestriction(finprop("amounts-extended"), "", 1000000, Double.POSITIVE_INFINITY, true)),
	finpropset("recipient-country"),
	finpropset("refYear"),
	Collections.singleton(Aggregate.COUNT))),

	be("What was the average aid to Egypt over the last 10 years?",true,
	new CubeTemplate(cube,
	new HashSet < > (Arrays.asList(countryRestriction("eg"), new TopRestriction(finprop("refYear"), "", 10, OrderModifier.DESC))),
	finpropset("amount"),
	Collections.emptySet(),
	Collections.singleton(Aggregate.AVG))),
//	//
//	//////
//	////// TODO "in europe" can't be expressed at the moment
//	////					// TODO different restriction type for year, is it implemented?
	be("Which were the top 10 aided countries in Europe in 2011?",false,
				new CubeTemplate(cube,
						new HashSet<>(Arrays.asList(new ValueRestriction(REF_YEAR,"2011"),new TopRestriction(finprop("amount"), "", 10, OrderModifier.DESC))),
						finpropset("recipient-country"),
						Collections.emptySet(),
						Collections.emptySet())),

	be("How much money give Finnish Red Cross on Malaria Control?",true,
	new CubeTemplate(cube,
	set(new ValueRestriction(finprop("channel-of-delivery-name"), "Finnish Red Cross"), sector("12262")),
	new HashSet < > (Arrays.asList(finprop("amount"), finprop("commitments"))),
	new HashSet < > (Arrays.asList(REF_YEAR, finprop("recipient-country"))),
	Collections.emptySet())),

	be("What is the amount of aid and the amount of commitments per country per year?",true,
	new CubeTemplate(cube,
	Collections.emptySet(),
	new HashSet < > (Arrays.asList(finprop("amount"), finprop("commitments"))),
	new HashSet < > (Arrays.asList(REF_YEAR, finprop("recipient-country"))),
	Collections.emptySet())),

	be("How much money did the Egyptian government receive for Disaster prevention and preparedness?",true,
	new CubeTemplate(cube,
	new HashSet < > (Arrays.asList(countryRestriction("eg"), new UriRestriction(finprop("sector"), "https://openspending.org/finland-aid/sector/74010"))),
	new HashSet < > (Arrays.asList(finprop("amount"))),
	Collections.emptySet(),
	Collections.singleton(Aggregate.SUM))),

////	 TODO can't express yet with templates
	be("What's the aid of the 10 richest and 10 poorest countries?",false,
	new CubeTemplate(cube,
	Collections.singleton(new TopRestriction(finprop("recipient-country"), "", 10, OrderModifier.DESC)),
	finpropset("amount"),
	Collections.emptySet(),
	Collections.emptySet())),

	be("How much did Uruguay receive?",true,
	new CubeTemplate(cube,
	Collections.singleton(countryRestriction("ug")),
	finpropset("amount"),
	Collections.emptySet(),
	Collections.singleton(Aggregate.SUM))),

	be("How much money goes into food crop production over time?",true,
	new CubeTemplate(cube,
	sectorset("31161"),
	finpropset("amount"),
	finpropset("refYear"),
	Collections.emptySet())),

	be("In South and Central Asia, how much biodiversity aid is there?",true,
	new CubeTemplate(cube,
	new HashSet < > (Arrays.asList(sector("31161"), countryRestriction("0"))),
	finpropset("biodiversity"),
	Collections.emptySet(),
	Collections.emptySet())),

	be("How much money Embassy of Finland contribute to Egyptian projects?",true,
	new CubeTemplate(cube,
	set(countryRestriction("eg"), new ValueRestriction(finprop("channel-of-delivery-name"), "Embassy of Finland")),
	new HashSet < > (Arrays.asList(finprop("amount"))),
	Collections.emptySet(),
	Collections.singleton(Aggregate.SUM))),

	be("How much education aid African countries get per year?",false,
	new CubeTemplate(cube,
	Collections.emptySet(),
	new HashSet < > (Arrays.asList(finprop("amount"), finprop("commitments"))),
	new HashSet < > (Arrays.asList(REF_YEAR, finprop("recipient-country"))),
	Collections.emptySet())),

	be("What is average aid amount per aid sector?",true,
	new CubeTemplate(cube,
	Collections.emptySet(),
	finpropset("amount"),
	finpropset("sector"),
	Collections.singleton(Aggregate.AVG))),

	be("How much money gets each country in 2008?",true,
	new CubeTemplate(cube,
	Collections.singleton(new ValueRestriction(cube.properties.get(LSO + "refYear"), "2008")),
	new HashSet < > (Arrays.asList(finprop("amount"), finprop("commitments"))),
	finpropset("recipient-country"),
	Collections.singleton(Aggregate.SUM))),

	be("How much extended amounts are given to Tajikistan for Rescheduling and refinancing?",true,
	new CubeTemplate(cube,
	set(countryRestriction("tj"), sector("60040")),
	finpropset("amounts-extended"),
	Collections.emptySet(),
	Collections.emptySet())),

	be("How much aid receives a country?",true,
	new CubeTemplate(cube,
	set(countryRestriction("tj"), sector("60040")),
	finpropset("amounts"),
	finpropset("recipient-country"),
	Collections.singleton(Aggregate.SUM))),

	be("How much money Nepal receives for Environmental policy and administrative management?",true,
	new CubeTemplate(cube,
	set(countryRestriction("np"), sector("41010")),
	finpropset("amounts"),
	finpropset("recipient-country"),
	Collections.singleton(Aggregate.SUM))),

	//new CubeTemplate(cube,
	//set(countryRestriction("tj"),sector("60040")),
	//finpropset("amounts"),
	//finpropset("recipient-country"),
	//Collections.emptySet(Aggregate.SUM))

	be("Which country has the highest amount of commitments?",false,
	// not possible? still countr properties...
						new CubeTemplate(cube,
					Collections.emptySet(),
					set(finprop("recipient-country"),finprop("commitments")),
					Collections.emptySet(),
					Collections.emptySet()))

//							Top 10 aid receivers in America?
//	//
////							new CubeTemplate(cube,
////					set(countryRestriction("tj"),sector("60040")),
////					finpropset("amounts"),
////					finpropset("recipient-country"),
////					Collections.emptySet(Aggregate.SUM)),

	//						//	Which type of sector is receiving the most in a particular country?
	//						// lässt sich als SPARQL query schwer (gar nicht?) ausdrücken
	//												new CubeTemplate(cube,
	//											Collections.singleton(countryRestriction("ug")),
	//											new HashSet<>(Arrays.asList(finprop("recipient-country"),finprop("sector"))),
	//											new HashSet<>(Arrays.asList(REF_YEAR,finprop("recipient-country"))),
	//											Collections.emptySet()),

);

}