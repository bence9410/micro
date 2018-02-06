package hu.beni.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MachineDTO {

	private Long identifier;

	private String fantasyName;

	private Integer size;

	private Integer price;

	private Integer numberOfSeats;

	private Integer minimumRequiredAge;

	private Integer ticketPrice;

	private String type;

}
