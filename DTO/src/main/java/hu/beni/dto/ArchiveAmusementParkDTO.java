package hu.beni.dto;

import java.util.List;
import java.util.Set;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ArchiveAmusementParkDTO {
	
	private Long identifier;

	private String name;

	private Integer capital;

	private Integer totalArea;

	private Integer entranceFee;

	private AddressDTO address;

	private List<GuestBookRegistryDTO> guestBookRegistry;

	private List<MachineDTO> machines;

	private Set<VisitorDTO> visitors;

}
