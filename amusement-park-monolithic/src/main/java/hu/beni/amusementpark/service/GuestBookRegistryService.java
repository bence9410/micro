package hu.beni.amusementpark.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import hu.beni.amusementpark.entity.GuestBookRegistry;

public interface GuestBookRegistryService {

	GuestBookRegistry findOne(Long guestBookRegistryId);

	GuestBookRegistry addRegistry(Long amusementParkId, String visitorEmail, String textOfRegistry);
	
	Page<GuestBookRegistry> findAll(Pageable pageable);

}
