package hu.beni.amusementpark.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import hu.beni.amusementpark.entity.GuestBookRegistry;
import hu.beni.amusementpark.repository.custom.GuestBookRegistryRepositoryCustom;

public interface GuestBookRegistryRepository
		extends JpaRepository<GuestBookRegistry, Long>, GuestBookRegistryRepositoryCustom {

}
