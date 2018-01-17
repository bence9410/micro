package hu.beni.amusement.park.repository;

import static hu.beni.amusement.park.constants.ParameterMappingConstants.*;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import hu.beni.amusement.park.entity.Visitor;

@Repository
public interface VisitorRepository extends JpaRepository<Visitor, Long> {
    
    @Query("Select v.spendingMoney from Visitor v where v.username = :#{principal.username}")
    public Integer findSpendingMoneyByUserName();

    @Query("Select count(v) from Visitor v where v.machine.id = :machineId")
    public Long countByMachineId(@Param(MACHINE_ID) Long machineId);

    @Query("Select count(v) from Visitor v where v.id = :visitorId and v.amusementPark is not null")
    public Long countByVisitorIdWhereAmusementParkIsNotNull(@Param(VISITOR_ID) Long visitorId);

    @Query("Select count(v) from Visitor v where v.amusementPark.id = :amusementParkId")
    public Long countByAmusementParkId(@Param(AMUSEMENT_PARK_ID) Long amusementParkId);

    @Query("Select v from Visitor v where v.machine.id = :machineId and v.id = :visitorId")
    public Visitor findByMachineIdAndVisitorId(@Param(MACHINE_ID) Long machineId, @Param(VISITOR_ID) Long visitorId);

    @Query("Select v from Visitor v where v.amusementPark.id = :amusementParkId and v.id = :visitorId")
    public Visitor findByAmusementParkIdAndVisitorId(@Param(AMUSEMENT_PARK_ID) Long amusementParkId, @Param(VISITOR_ID) Long visitorId);

}
