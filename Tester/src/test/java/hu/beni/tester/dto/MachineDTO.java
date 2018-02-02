package hu.beni.tester.dto;

import hu.beni.tester.enums.MachineType;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;

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

	private MachineType type;

	@Tolerate
	protected MachineDTO() {
		super();
	}

}
