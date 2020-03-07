package hu.beni.amusementpark.dto.request;

import hu.beni.amusementpark.enums.MachineType;
import lombok.Data;

@Data
public class MachineSearchRequestDto {

	private Long amusementParkId;

	private String fantasyName;

	private Integer sizeMin;

	private Integer sizeMax;

	private Integer priceMin;

	private Integer priceMax;

	private Integer numberOfSeatsMin;

	private Integer numberOfSeatsMax;

	private Integer minimumRequiredAgeMin;

	private Integer minimumRequiredAgeMax;

	private Integer ticketPriceMin;

	private Integer ticketPriceMax;

	private MachineType type;

}
