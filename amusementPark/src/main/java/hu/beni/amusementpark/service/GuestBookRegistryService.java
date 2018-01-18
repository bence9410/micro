package hu.beni.amusementpark.service;

import hu.beni.amusementpark.entity.GuestBookRegistry;

public interface GuestBookRegistryService {
	
	GuestBookRegistry findOne(Long guestBookRegistryId);
	
	GuestBookRegistry addRegistry(Long amusementParkId, Long visitorId, String textOfRegistry);

}
