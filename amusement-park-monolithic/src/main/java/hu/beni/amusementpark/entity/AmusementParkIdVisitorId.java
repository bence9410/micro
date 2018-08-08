package hu.beni.amusementpark.entity;

import java.io.Serializable;

import javax.persistence.Embeddable;

import lombok.Data;

@Embeddable
@Data
public class AmusementParkIdVisitorId implements Serializable {

	private static final long serialVersionUID = 8789414110410471281L;

	private Long amusementParkId;

	private Long visitorId;

}
