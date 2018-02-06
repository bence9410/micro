package hu.beni.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AddressDTO {

	private Long identifier;

    private String country;

    private String zipCode;
    
    private String city;

    private String street;

    private String houseNumber;
    
}
