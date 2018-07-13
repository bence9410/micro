package hu.beni.clientsupport.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class VisitorGetOnMachineEventDTO extends VisitorSpentMoneyInParkEvent {

	private static final long serialVersionUID = -7446143005176911615L;

	private final Long machineId;

	public VisitorGetOnMachineEventDTO(Long amusementParkId, Long visitorId, Integer spentMoney, Long machineId) {
		super(amusementParkId, visitorId, spentMoney);
		this.machineId = machineId;
	}

}
