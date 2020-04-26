package hu.beni.amusementpark.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnore;

import hu.beni.amusementpark.enums.MachineType;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MachineSearchResponseDto {

	@JsonIgnore
	private Long id;

	private String fantasyName;

	private Integer size;

	private Integer price;

	private Integer numberOfSeats;

	private Integer minimumRequiredAge;

	private Integer ticketPrice;

	private MachineType type;
}
