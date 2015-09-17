package org.aksw.cubeqa.scripts;

import java.util.*;
import java.util.stream.Collectors;
import org.aksw.cubeqa.Cube;
import org.aksw.cubeqa.index.CubeIndex;

public class FillCubeIndex
{
	static String[] cubeNames = {"town_of_cary_expenditures","town_of_cary_revenues","city-of-whiteacre-spending","maldives_proposed_expenditure_2015","ministry_of_health","newcastle-city-council-payments-over-500","uk-local-walthamforest","cheshire_west_and_chester_april_2013","city-of-redacre-spending","wandsworthspending_2013","iw-council-spending-2012-13-test","city-of-springfield-budget","pscs_ca_cities","dc-city-salaries","big-lottery-fund-grants","uk-local-gloucestershirev1","ukgov-finances-cra","iati_cordaid_af","ca-local-toronto","ie_charity_exp","frontex","dcc_exp_budget2013","financial_aid","concern2012","dc-vendors-contractors","cameroon_visualisation","nominettrust_funding","618ac3ec98384f44a9ef142356ce476d","propbudg13","nyc-council-member-items","fingal_exp_budget","cm-nwr-investments","ug_budget_subcategories","faith","finland-aid","pvd2014proposed","pbw-ct","trends_in_civi_tech_open_gov","e27f4ef7601446798cfa733a06cea8d9","f0bd947d9854445987d6ece304840a3c","scottish-spending-jan13","oakland-adopted-budget-fy-2011-13-expenditures","allexp13budg","lobbying_dataset_qy","mcc_spend","alb_planned_budget_2013","ke-county-exp","al_planned_budget_2007to2013","external-debts","ddc94682cc95482a8deefc60596686fc"};

	public static void main(String[] args)
	{
		System.out.println("type fillcube to continue");
		try(Scanner in = new Scanner(System.in))
		{if(!in.nextLine().equals("fillcube")) {System.out.println("wrong phrase. terminated.");return;}}
		Set<Cube> cubes = Arrays.stream(cubeNames).map(Cube::getInstance).collect(Collectors.toSet());
		CubeIndex.INSTANCE.fill(cubes);
	}
}