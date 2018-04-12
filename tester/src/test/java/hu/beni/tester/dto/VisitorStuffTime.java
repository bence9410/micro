package hu.beni.tester.dto;

import java.util.List;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class VisitorStuffTime {
	
	private final long wholeTime;
	
	private final List<Long> tenParkTimes;
	
	private final List<Long> oneParkTimes;

}
