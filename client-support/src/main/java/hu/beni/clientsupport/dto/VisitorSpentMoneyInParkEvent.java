package hu.beni.clientsupport.dto;

import java.io.Serializable;

import lombok.Data;

@Data
public abstract class VisitorSpentMoneyInParkEvent implements Serializable {

	private static final long serialVersionUID = 2488521250642328750L;

	private final Long amusementParkId;

	private final String visitorEmail;

	private final Integer spentMoney;

	protected VisitorSpentMoneyInParkEvent(Long amusementParkId, String visitorEmail, Integer spentMoney) {
		this.amusementParkId = amusementParkId;
		this.visitorEmail = visitorEmail;
		this.spentMoney = spentMoney;
	}
}
