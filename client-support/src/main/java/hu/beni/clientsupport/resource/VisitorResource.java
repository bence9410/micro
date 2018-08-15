package hu.beni.clientsupport.resource;

import java.io.Serializable;
import java.time.LocalDate;
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
public class VisitorResource extends ResourceSupport implements Serializable {

	private static final long serialVersionUID = -426306691990271010L;

	private Long identifier;

	private String name;

	private String username;

	private String password;

	private String confirmPassword;

	private String authority;

	private LocalDate dateOfBirth;

	private Integer spendingMoney;

	private String state;

	@Builder
	public VisitorResource(Long identifier, String name, String username, String password, String confirmPassword,
			String authority, LocalDate dateOfBirth, Integer spendingMoney, String state, Link[] links) {
		super();
		this.identifier = identifier;
		this.name = name;
		this.username = username;
		this.password = password;
		this.confirmPassword = confirmPassword;
		this.authority = authority;
		this.dateOfBirth = dateOfBirth;
		this.spendingMoney = spendingMoney;
		this.state = state;
		Optional.ofNullable(links).ifPresent(this::add);
	}

}
