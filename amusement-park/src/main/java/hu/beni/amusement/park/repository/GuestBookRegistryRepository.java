package hu.beni.amusement.park.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import hu.beni.amusement.park.entity.GuestBookRegistry;

@Repository
public interface GuestBookRegistryRepository extends JpaRepository<GuestBookRegistry, Long>{

}
