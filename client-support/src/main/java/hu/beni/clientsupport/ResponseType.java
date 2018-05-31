package hu.beni.clientsupport;

import java.util.HashMap;
import java.util.Map;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.Resources;
import org.springframework.hateoas.mvc.TypeReferences.PagedResourcesType;

import hu.beni.clientsupport.resource.AmusementParkResource;
import hu.beni.clientsupport.resource.GuestBookRegistryResource;
import hu.beni.clientsupport.resource.MachineResource;
import hu.beni.clientsupport.resource.VisitorResource;

public class ResponseType {

	private static final Map<Class, PagedResourcesType> PAGED_TYPES;

	static {
		PAGED_TYPES = new HashMap<>();
		PAGED_TYPES.put(AmusementParkResource.class, new PagedResourcesType<AmusementParkResource>() {
		});

		PAGED_TYPES.put(VisitorResource.class, new PagedResourcesType<VisitorResource>() {
		});
	}

	public static final <T> PagedResourcesType<T> getPagedType(Class<T> clazz) {
		return PAGED_TYPES.get(clazz);
	}

	//@formatter:off
	public static final ParameterizedTypeReference<AmusementParkResource> AMUSEMENT_PARK_TYPE = 
			new ParameterizedTypeReference<AmusementParkResource>() {};
			
	public static final ParameterizedTypeReference<MachineResource> MACHINE_TYPE = 
			new ParameterizedTypeReference<MachineResource>() {};

	public static final ParameterizedTypeReference<Resources<MachineResource>> RESOURCES_MACHINE_TYPE = 
			new ParameterizedTypeReference<Resources<MachineResource>>() {};
		
	public static final ParameterizedTypeReference<VisitorResource> VISITOR_TYPE = 
			new ParameterizedTypeReference<VisitorResource>() {};			

	public static final ParameterizedTypeReference<GuestBookRegistryResource> GUEST_BOOK_REGISTRY_TYPE =
			new ParameterizedTypeReference<GuestBookRegistryResource>() {};
	//@formatter:on

	private ResponseType() {
		super();
	}

}
