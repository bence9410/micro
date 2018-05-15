package hu.beni.clientsupport.resource;

import java.io.Serializable;
import java.util.Optional;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.ResourceSupport;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class MachineResource extends ResourceSupport implements Serializable {

	private static final long serialVersionUID = 1009869702988779913L;

	private Long identifier;

	private String fantasyName;

	private Integer size;

	private Integer price;

	private Integer numberOfSeats;

	private Integer minimumRequiredAge;

	private Integer ticketPrice;

	private String type;

	public MachineResource() {
		super();
	}

	@Builder
	public MachineResource(Long identifier, String fantasyName, Integer size, Integer price, Integer numberOfSeats,
			Integer minimumRequiredAge, Integer ticketPrice, String type, Link[] links) {
		super();
		this.identifier = identifier;
		this.fantasyName = fantasyName;
		this.size = size;
		this.price = price;
		this.numberOfSeats = numberOfSeats;
		this.minimumRequiredAge = minimumRequiredAge;
		this.ticketPrice = ticketPrice;
		this.type = type;
		Optional.ofNullable(links).ifPresent(this::add);
	}

}
