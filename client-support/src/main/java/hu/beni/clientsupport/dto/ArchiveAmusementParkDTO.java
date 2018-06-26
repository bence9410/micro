package hu.beni.clientsupport.dto;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ArchiveAmusementParkDTO implements Serializable {

	private static final long serialVersionUID = -8578786392708912052L;

	private Long identifier;

	private String name;

	private Integer capital;

	private Integer totalArea;

	private Integer entranceFee;

	private AddressDTO address;

	private List<ArchiveGuestBookRegistryDTO> guestBookRegistries;

	private List<ArchiveMachineDTO> machines;

	private Set<ArchiveVisitorDTO> knownVisitors;

}
