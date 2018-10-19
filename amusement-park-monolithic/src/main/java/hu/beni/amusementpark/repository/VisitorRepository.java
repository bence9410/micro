package hu.beni.amusementpark.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import hu.beni.amusementpark.entity.Visitor;

public interface VisitorRepository extends JpaRepository<Visitor, String> {

	@Query("Select count(*) from Visitor v where v.email = :visitorEmail")
	Long countByEmail(String visitorEmail);

	@Query("Select v from Visitor v where v.email = :visitorEmail")
	Optional<Visitor> findByEmail(String visitorEmail);

	@Modifying
	@Query("Update Visitor v set v.spendingMoney = v.spendingMoney + :ammount where v.email = :visitorEmail")
	void incrementSpendingMoneyByEmail(Integer ammount, String visitorEmail);

	@Query("Select count(v) from Visitor v where v.machine.id = :machineId")
	Long countByMachineId(Long machineId);

	@Query("Select count(v) from Visitor v where v.amusementPark.id = :amusementParkId")
	Long countByAmusementParkId(Long amusementParkId);

	@Query("Select v from Visitor v where v.machine.id = :machineId and v.email = :visitorEmail")
	Optional<Visitor> findByMachineIdAndVisitorEmail(Long machineId, String visitorEmail);

	@Query("Select v from Visitor v where v.amusementPark.id = :amusementParkId and v.email = :visitorEmail")
	Optional<Visitor> findByAmusementParkIdAndVisitorEmail(Long amusementParkId, String visitorEmail);

}
