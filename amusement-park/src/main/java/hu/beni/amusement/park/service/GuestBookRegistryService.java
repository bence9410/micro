package hu.beni.amusement.park.service;

import hu.beni.amusement.park.entity.GuestBookRegistry;

public interface GuestBookRegistryService {
	
	GuestBookRegistry findOne(Long guestBookRegistryId);
	
	GuestBookRegistry addRegistry(Long amusementParkId, Long visitorId, String textOfRegistry);

}
