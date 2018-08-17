package hu.beni.clientsupport.resource;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Optional;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import javax.validation.constraints.Past;
import javax.validation.constraints.Size;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.ResourceSupport;

import hu.beni.clientsupport.constraint.PasswordConfirmPasswordSameConstraint;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@PasswordConfirmPasswordSameConstraint
public class VisitorResource extends ResourceSupport implements Serializable {

	private static final long serialVersionUID = -426306691990271010L;

	@Null
	private Long identifier;

	@NotNull
	@Size(min = 5, max = 25)
	private String name;

	@NotNull
	@Size(min = 5, max = 25)
	private String username;

	@NotNull
	@Size(min = 5, max = 25)
	private String password;

	@NotNull
	@Size(min = 5, max = 25)
	private String confirmPassword;

	@Null
	private String authority;

	@NotNull
	@Past
	private LocalDate dateOfBirth;

	@Null
	private Integer spendingMoney;

	@Null
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
