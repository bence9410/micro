package hu.beni.amusementpark.repository;

import static hu.beni.amusementpark.constants.ParameterMappingConstants.AMUSEMENT_PARK_ID;
import static hu.beni.amusementpark.constants.ParameterMappingConstants.MACHINE_ID;
import static hu.beni.amusementpark.constants.ParameterMappingConstants.VISITOR_ID;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import hu.beni.amusementpark.entity.Visitor;

public interface VisitorRepository extends JpaRepository<Visitor, Long> {

	@Query("Select v.spendingMoney from Visitor v where v.username = :#{principal.username}")
	Optional<Integer> findSpendingMoneyByUsername();

	@Query("Select count(v) from Visitor v where v.machine.id = :machineId")
	Long countByMachineId(@Param(MACHINE_ID) Long machineId);

	@Query("Select count(v) from Visitor v where v.amusementPark.id = :amusementParkId")
	Long countByAmusementParkId(@Param(AMUSEMENT_PARK_ID) Long amusementParkId);

	@Query("Select v from Visitor v where v.machine.id = :machineId and v.id = :visitorId")
	Optional<Visitor> findByMachineIdAndVisitorId(@Param(MACHINE_ID) Long machineId, @Param(VISITOR_ID) Long visitorId);

	@Query("Select v from Visitor v where v.amusementPark.id = :amusementParkId and v.id = :visitorId")
	Optional<Visitor> findByAmusementParkIdAndVisitorId(@Param(AMUSEMENT_PARK_ID) Long amusementParkId,
			@Param(VISITOR_ID) Long visitorId);

}
