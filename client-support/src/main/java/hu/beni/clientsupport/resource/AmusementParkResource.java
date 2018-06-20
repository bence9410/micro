package hu.beni.clientsupport.resource;

import java.io.Serializable;
import java.util.Optional;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.ResourceSupport;

import hu.beni.clientsupport.dto.AddressDTO;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class AmusementParkResource extends ResourceSupport implements Serializable {

	private static final long serialVersionUID = -7411398427560874485L;

	private Long identifier;

	private String name;

	private Integer capital;

	private Integer totalArea;

	private Integer entranceFee;

	private AddressDTO address;

	@Builder
	public AmusementParkResource(Long identifier, String name, Integer capital, Integer totalArea, Integer entranceFee,
			AddressDTO address, Link[] links) {
		super();
		this.identifier = identifier;
		this.name = name;
		this.capital = capital;
		this.totalArea = totalArea;
		this.entranceFee = entranceFee;
		this.address = address;
		Optional.ofNullable(links).ifPresent(this::add);
	}

}
