package hu.beni.amusementpark.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;

@Entity
@Builder
@Data
public class ActiveVisitor implements Serializable{

	@Id
	private Long id;
	
	@MapsId
	@OneToOne(fetch = FetchType.LAZY)
	private Visitor visitor; 
	
	@ManyToOne
	private AmusementPark amusementPark;
	
	@Tolerate
	protected ActiveVisitor() {
		super();
	}
	
}
