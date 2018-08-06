package hu.beni.amusementpark.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import hu.beni.amusementpark.entity.Visitor;

public interface VisitorRepository extends JpaRepository<Visitor, Long> {

	@Query("Select v.spendingMoney from Visitor v where v.username = :#{principal.username}")
	Integer findSpendingMoneyByUsername();

	@Query("Select count(v) from Visitor v where v.machine.id = :machineId")
	Long countByMachineId(Long machineId);

	@Query("Select count(v) from Visitor v where v.amusementPark.id = :amusementParkId")
	Long countByAmusementParkId(Long amusementParkId);

	@Query("Select v from Visitor v where v.machine.id = :machineId and v.id = :visitorId and v.username = :#{principal.username}")
	Optional<Visitor> findByMachineIdAndVisitorId(Long machineId, Long visitorId);

	@Query("Select v from Visitor v where v.amusementPark.id = :amusementParkId and v.id = :visitorId and v.username = :#{principal.username}")
	Optional<Visitor> findByAmusementParkIdAndVisitorId(Long amusementParkId, Long visitorId);

}
