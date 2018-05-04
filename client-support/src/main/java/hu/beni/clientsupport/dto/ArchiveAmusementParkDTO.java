package hu.beni.clientsupport.dto;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

import hu.beni.clientsupport.resource.GuestBookRegistryResource;
import hu.beni.clientsupport.resource.MachineResource;
import hu.beni.clientsupport.resource.VisitorResource;
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

	private List<GuestBookRegistryResource> guestBookRegistry;

	private List<MachineResource> machines;

	private Set<VisitorResource> visitors;

}
