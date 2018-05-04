package hu.beni.clientsupport.dto;

import java.io.Serializable;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AddressDTO implements Serializable {

	private static final long serialVersionUID = 967545444061352211L;

	private Long identifier;

	private String country;

	private String zipCode;

	private String city;

	private String street;

	private String houseNumber;

}
