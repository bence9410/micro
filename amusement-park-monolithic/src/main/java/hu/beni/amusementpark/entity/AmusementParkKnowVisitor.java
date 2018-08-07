package hu.beni.amusementpark.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@EqualsAndHashCode(of = "id")
public class AmusementParkKnowVisitor implements Serializable {

	private static final long serialVersionUID = 8289304865876769056L;

	@Id
	@GeneratedValue
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	private AmusementPark amusementPark;

	@ManyToOne
	private Visitor visitor;

	public AmusementParkKnowVisitor(AmusementPark amusementPark, Visitor visitor) {
		super();
		this.amusementPark = amusementPark;
		this.visitor = visitor;
	}

}
