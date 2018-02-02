package hu.beni.tester.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TimeTo {
	
	private List<Long> createAmusementParksWithMachines;

	private List<Long> findAllParksPagedBeforeVisitorStuff;

	private List<Long> wholeVisitorStuff;
	
	private List<Long> oneParkVisitorStuff;

	private List<Long> findAllParksPagedAfterVisitorStuff;

	private List<Long> findAllVisitorsPaged;

	private DeleteTime deleteParks;

	private DeleteTime deleteVisitors;
	
}