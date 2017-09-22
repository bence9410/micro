package hu.beni.amusementpark.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import static hu.beni.amusementpark.constants.ParameterMappingConstants.*;
import hu.beni.amusementpark.entity.Visitor;

@Repository
public interface VisitorRepository extends JpaRepository<Visitor, Long> {

    @Query("Select count(v) from Visitor v where v.machine.id = :machineId")
    public Long countByMachineId(@Param(MACHINE_ID) Long machineId);

    @Query("Select v from Visitor v where v.machine.id = :machineId and v.id = :visitorId")
    public Visitor findByMachineIdAndVisitorId(@Param(MACHINE_ID) Long machineId, @Param(VISITOR_ID) Long visitorId);
    
    @Query("Select v from Visitor v where v.amusementPark.id = :amusementParkId and v.id = :visitorId")
    public Visitor findByAmusementParkIdAndVisitorId(@Param(AMUSEMENT_PARK_ID) Long amusementParkId, @Param(VISITOR_ID) Long visitorId);

    @Query("Select new hu.beni.amusementpark.entity.Visitor(v.id) from Visitor v where v.amusementPark.id = :amusementParkId and v.id = :visitorId")
    public Visitor findByAmusementParkIdAndVisitorIdReadOnlyId(@Param(AMUSEMENT_PARK_ID) Long amusementParkId, @Param(VISITOR_ID)Long visitorId);
    
}
