package hu.beni.amusementpark.entity;

import java.io.Serializable;

import javax.persistence.Embeddable;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Builder;
import lombok.Data;

@Embeddable
@Data
@Builder
public class Address implements Serializable {

	private static final long serialVersionUID = 5753682920839496113L;

	@NotNull
	@Size(min = 3, max = 15)
	private String country;

	@NotNull
	@Size(min = 3, max = 10)
	private String zipCode;

	@NotNull
	@Size(min = 3, max = 15)
	private String city;

	@NotNull
	@Size(min = 5, max = 25)
	private String street;

	@NotEmpty
	@Size(max = 5)
	private String houseNumber;

}
