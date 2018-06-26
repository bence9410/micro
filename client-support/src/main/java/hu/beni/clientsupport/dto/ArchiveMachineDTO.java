package hu.beni.clientsupport.dto;

import java.io.Serializable;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ArchiveMachineDTO implements Serializable {

	private static final long serialVersionUID = -4597391194636515789L;

	private Long identifier;

	private String fantasyName;

	private Integer size;

	private Integer price;

	private Integer numberOfSeats;

	private Integer minimumRequiredAge;

	private Integer ticketPrice;

	private String type;

}
