package hu.beni.amusementpark.dto.request;

import lombok.Data;

@Data
public class AmusementParkSearchRequestDto {

	private String name;

	private Integer capitalMin;

	private Integer capitalMax;

	private Integer totalAreaMin;

	private Integer totalAreaMax;

	private Integer entranceFeeMin;

	private Integer entranceFeeMax;

}
