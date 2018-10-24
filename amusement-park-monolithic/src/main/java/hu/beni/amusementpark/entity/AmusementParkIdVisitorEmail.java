package hu.beni.amusementpark.entity;

import java.io.Serializable;

import javax.persistence.Embeddable;

import lombok.Data;

@Embeddable
@Data
public class AmusementParkIdVisitorEmail implements Serializable {

	private static final long serialVersionUID = 8789414110410471281L;

	private Long amusementParkId;

	private String visitorEmail;

}
