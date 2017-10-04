package hu.beni.amusementpark.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import hu.beni.amusementpark.entity.ActiveVisitor;

@Repository
public interface ActiveVisitorRepository extends JpaRepository<ActiveVisitor, Long>{
	
	

}
