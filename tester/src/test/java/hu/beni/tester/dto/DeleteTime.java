package hu.beni.tester.dto;

import java.util.List;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class DeleteTime {

	private final long wholeTime;

	private final List<Long> tenDeleteTimes;

}
