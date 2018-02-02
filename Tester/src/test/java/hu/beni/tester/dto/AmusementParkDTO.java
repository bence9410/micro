package hu.beni.tester.dto;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;

@Data
@Builder
public class AmusementParkDTO {

	private Long identifier;

	private String name;

	private Integer capital;

	private Integer totalArea;

	private Integer entranceFee;

	private AddressDTO address;

	@Tolerate
	protected AmusementParkDTO() {
		super();
	}

}
