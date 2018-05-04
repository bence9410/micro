package hu.beni.clientsupport.resource;

import java.io.Serializable;
import java.time.LocalDate;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.ResourceSupport;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class VisitorResource extends ResourceSupport implements Serializable {

	private static final long serialVersionUID = -426306691990271010L;

	private Long identifier;

	private String name;

	private LocalDate dateOfBirth;

	private Integer spendingMoney;

	private String state;

	public VisitorResource() {
		super();
	}

	@Builder
	public VisitorResource(Long identifier, String name, LocalDate dateOfBirth, Integer spendingMoney, String state,
			Link[] links) {
		super();
		this.identifier = identifier;
		this.name = name;
		this.dateOfBirth = dateOfBirth;
		this.spendingMoney = spendingMoney;
		this.state = state;
		add(links);
	}

}
