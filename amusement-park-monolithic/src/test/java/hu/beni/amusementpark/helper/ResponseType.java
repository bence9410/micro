package hu.beni.amusementpark.helper;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.mvc.TypeReferences.PagedResourcesType;
import hu.beni.clientsupport.resource.AmusementParkResource;
import hu.beni.clientsupport.resource.GuestBookRegistryResource;
import hu.beni.clientsupport.resource.MachineResource;
import hu.beni.clientsupport.resource.VisitorResource;

public class ResponseType {

	//@formatter:off
	public static final ParameterizedTypeReference<AmusementParkResource> AMUSEMENT_PARK_TYPE = 
			new ParameterizedTypeReference<AmusementParkResource>() {};
			
	public static final PagedResourcesType<AmusementParkResource> PAGED_AMUSEMENT_PARK_TYPE = 
			new PagedResourcesType<AmusementParkResource>() {};
	
	public static final ParameterizedTypeReference<MachineResource> MACHINE_TYPE = 
			new ParameterizedTypeReference<MachineResource>() {};

	public static final ParameterizedTypeReference<VisitorResource> VISITOR_TYPE = 
			new ParameterizedTypeReference<VisitorResource>() {};

	public static final ParameterizedTypeReference<GuestBookRegistryResource> GUEST_BOOK_REGISTRY_TYPE =
			new ParameterizedTypeReference<GuestBookRegistryResource>() {};
	//@formatter:on

}
