package hu.beni.amusementpark.entity;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Tolerate;

@Entity
@Getter
@Setter
@Builder
@EqualsAndHashCode(of = {"id", "textOfRegistry", "dateOfRegistry"})
public class GuestBookRegistry implements Serializable{

	@Id
	@GeneratedValue
	@JsonProperty("identifier")
	private Long id;
	
	@NotNull
	@Size(min = 5, max = 100)
	private String textOfRegistry;
	
	@CreationTimestamp
	private Timestamp dateOfRegistry;
	
	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JsonIgnore
	private Visitor visitor;
	
	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JsonIgnore
	private AmusementPark amusementPark;
	
	@Tolerate
	protected GuestBookRegistry() {
		super();
	}
	
}
