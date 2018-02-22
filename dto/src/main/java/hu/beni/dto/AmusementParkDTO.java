package hu.beni.dto;

import java.io.Serializable;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AmusementParkDTO implements Serializable{

	private static final long serialVersionUID = -7411398427560874485L;

	private Long identifier;

	private String name;

	private Integer capital;

	private Integer totalArea;

	private Integer entranceFee;

	private AddressDTO address;

}
