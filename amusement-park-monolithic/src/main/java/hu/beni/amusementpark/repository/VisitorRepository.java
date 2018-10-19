package hu.beni.amusementpark.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import hu.beni.amusementpark.entity.Visitor;

public interface VisitorRepository extends JpaRepository<Visitor, Long> {

	@Query("Select count(*) from Visitor v where v.email = :email")
	Long countByEmail(String email);

	@Query("Select v from Visitor v where v.email = :email")
	Optional<Visitor> findByEmail(String email);

	@Modifying
	@Query("Update Visitor v set v.spendingMoney = v.spendingMoney + :ammount where v.email = :#{principal.username}")
	void incrementSpendingMoneyForLoggedInVisitor(Integer ammount);

	@Query("Select count(v) from Visitor v where v.machine.id = :machineId")
	Long countByMachineId(Long machineId);

	@Query("Select count(v) from Visitor v where v.amusementPark.id = :amusementParkId")
	Long countByAmusementParkId(Long amusementParkId);

	@Query("Select v from Visitor v where v.machine.id = :machineId and v.id = :visitorId and v.email = :#{principal.username}")
	Optional<Visitor> findByMachineIdAndVisitorId(Long machineId, Long visitorId);

	@Query("Select v from Visitor v where v.amusementPark.id = :amusementParkId and v.id = :visitorId and v.email = :#{principal.username}")
	Optional<Visitor> findByAmusementParkIdAndVisitorId(Long amusementParkId, Long visitorId);

}
