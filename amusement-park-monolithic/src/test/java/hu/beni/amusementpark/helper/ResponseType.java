package hu.beni.amusementpark.helper;

import org.springframework.hateoas.mvc.TypeReferences.PagedResourcesType;
import org.springframework.hateoas.mvc.TypeReferences.ResourceType;
import hu.beni.amusementpark.entity.GuestBookRegistry;
import hu.beni.amusementpark.entity.Machine;
import hu.beni.amusementpark.entity.Visitor;
import hu.beni.dto.AmusementParkDTO;

public class ResponseType {

	//@formatter:off
	public static final ResourceType<AmusementParkDTO> AMUSEMENT_PARK_TYPE = new ResourceType<AmusementParkDTO>() {};
			
	public static final PagedResourcesType<AmusementParkDTO> PAGED_AMUSEMENT_PARK_TYPE = new PagedResourcesType<AmusementParkDTO>() {};
	
	public static final ResourceType<Machine> MACHINE_TYPE = new ResourceType<Machine>() {};

	public static final ResourceType<Visitor> VISITOR_TYPE = new ResourceType<Visitor>() {};

	public static final ResourceType<GuestBookRegistry> GUEST_BOOK_REGISTRY_TYPE = new ResourceType<GuestBookRegistry>() {};
	//@formatter:on

}
