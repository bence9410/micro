package hu.beni.clientsupport.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class VisitorEnterParkEventDTO extends VisitorSpentMoneyInParkEvent {

	private static final long serialVersionUID = -5424568354736327084L;

	public VisitorEnterParkEventDTO(Long amusementParkId, String visitorEmail, Integer spentMoney) {
		super(amusementParkId, visitorEmail, spentMoney);
	}

}
