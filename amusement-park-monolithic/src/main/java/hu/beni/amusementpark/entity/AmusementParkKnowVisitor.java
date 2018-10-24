package hu.beni.amusementpark.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;

import org.hibernate.annotations.CreationTimestamp;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@EqualsAndHashCode(of = "id")
public class AmusementParkKnowVisitor implements Serializable {

	private static final long serialVersionUID = 8289304865876769056L;

	@EmbeddedId
	private AmusementParkIdVisitorEmail id = new AmusementParkIdVisitorEmail();

	@CreationTimestamp
	private LocalDateTime dateOfFirstEnter;

	@MapsId("amusementParkId")
	@ManyToOne(fetch = FetchType.LAZY)
	private AmusementPark amusementPark;

	@MapsId("visitorEmail")
	@ManyToOne
	private Visitor visitor;

	public AmusementParkKnowVisitor(AmusementPark amusementPark, Visitor visitor) {
		super();
		this.amusementPark = amusementPark;
		this.visitor = visitor;
	}

}
