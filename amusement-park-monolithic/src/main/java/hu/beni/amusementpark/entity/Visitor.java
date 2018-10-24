package hu.beni.amusementpark.entity;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import javax.validation.constraints.Size;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.validator.constraints.Range;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Tolerate;

@Entity
@Getter
@Setter
@Builder
@EqualsAndHashCode(of = "email")
public class Visitor implements Serializable {

	private static final long serialVersionUID = -2955989272392888202L;

	@Id
	@NotNull
	@Email(regexp = ".+@.+\\..+")
	private String email;

	@NotNull
	@Size(min = 60, max = 60)
	private String password;

	@NotNull
	@Size(min = 5, max = 25)
	private String authority;

	@NotNull
	@Past
	private LocalDate dateOfBirth;

	@CreationTimestamp
	private LocalDateTime dateOfSignUp;

	@NotNull
	@Range(min = 50, max = Integer.MAX_VALUE)
	private Integer spendingMoney;

	@Lob
	private String photo;

	@ManyToOne(fetch = FetchType.LAZY)
	private AmusementPark amusementPark;

	@ManyToOne(fetch = FetchType.LAZY)
	private Machine machine;

	@OneToMany(mappedBy = "visitor")
	private List<GuestBookRegistry> guestBookRegistries;

	@OneToMany(mappedBy = "visitor")
	private Set<AmusementParkKnowVisitor> knownAmusementParks;

	@Tolerate
	protected Visitor() {
		super();
	}

}
