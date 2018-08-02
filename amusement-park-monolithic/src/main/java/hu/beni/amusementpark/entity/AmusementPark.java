package hu.beni.amusementpark.entity;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.MapsId;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

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
@EqualsAndHashCode(of = { "id", "name", "capital", "totalArea", "entranceFee" })
public class AmusementPark implements Serializable {

	private static final long serialVersionUID = -2064262013451563720L;

	@Id
	private Long id;

	@NotNull
	@Size(min = 5, max = 20)
	private String name;

	@NotNull
	@Range(min = 500, max = 50000)
	private Integer capital;

	@NotNull
	@Range(min = 50, max = 2000)
	private Integer totalArea;

	@NotNull
	@Range(min = 5, max = 200)
	private Integer entranceFee;

	@MapsId
	@Valid
	@NotNull
	@OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, optional = false)
	private Address address;

	@OneToMany(mappedBy = "amusementPark", cascade = CascadeType.REMOVE)
	private List<GuestBookRegistry> guestBookRegistries;

	@OneToMany(mappedBy = "amusementPark", cascade = CascadeType.REMOVE)
	private List<Machine> machines;

	@OneToMany(mappedBy = "amusementPark")
	private List<Visitor> activeVisitors;

	@ManyToMany
	@JoinTable(name = "amusement_park_visitor", //@formatter:off
		joinColumns = @JoinColumn(name = "amusement_park_id"),
		inverseJoinColumns = @JoinColumn(name = "visitor_id")) //@formatter:on
	private Set<Visitor> knownVisitors;

	@Tolerate
	protected AmusementPark() {
		super();
	}

	@Tolerate
	public AmusementPark(Long id) {
		this.id = id;
	}

	@Tolerate
	public AmusementPark(Long id, Integer entranceFee) {
		this.id = id;
		this.entranceFee = entranceFee;
	}

	@Tolerate
	public AmusementPark(Long id, Integer capital, Integer totalArea) {
		this.id = id;
		this.capital = capital;
		this.totalArea = totalArea;
	}
}
