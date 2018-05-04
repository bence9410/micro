package hu.beni.clientsupport.dto;

import java.io.Serializable;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.ResourceSupport;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class AmusementParkDTO extends ResourceSupport implements Serializable {

	private static final long serialVersionUID = -7411398427560874485L;

	private Long identifier;

	private String name;

	private Integer capital;

	private Integer totalArea;

	private Integer entranceFee;

	private AddressDTO address;

	public AmusementParkDTO() {
		super();
	}

	@Builder
	public AmusementParkDTO(Long identifier, String name, Integer capital, Integer totalArea, Integer entranceFee,
			AddressDTO address, Link[] links) {
		super();
		this.identifier = identifier;
		this.name = name;
		this.capital = capital;
		this.totalArea = totalArea;
		this.entranceFee = entranceFee;
		this.address = address;
		add(links);
	}

}
