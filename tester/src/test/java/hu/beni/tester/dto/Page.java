package hu.beni.tester.dto;

import java.util.List;

import lombok.Data;

@Data
public class Page<T> {

	private List<T> content;
	
	private boolean last;
	
}
