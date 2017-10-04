package hu.beni.amusementpark.entity;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

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
	
	private String textOfRegistry;
	
	@CreationTimestamp
	private Timestamp dateOfRegistry;
	
	@ManyToOne
	@JsonIgnore
	private Visitor visitor;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JsonIgnore
	private AmusementPark amusementPark;
	
	@Tolerate
	protected GuestBookRegistry() {
		super();
	}
	
}
