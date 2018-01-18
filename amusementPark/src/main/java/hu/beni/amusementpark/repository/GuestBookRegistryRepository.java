package hu.beni.amusementpark.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import hu.beni.amusementpark.entity.GuestBookRegistry;

@Repository
public interface GuestBookRegistryRepository extends JpaRepository<GuestBookRegistry, Long>{

}
