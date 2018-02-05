package hu.beni.dto;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;

@Data
@Builder
public class AddressDTO {

	private Long identifier;

    private String country;

    private String zipCode;
    
    private String city;

    private String street;

    private String houseNumber;
    
    @Tolerate
    protected AddressDTO() {
        super();
    }
	
}
