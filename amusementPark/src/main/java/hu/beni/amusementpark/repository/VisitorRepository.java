package hu.beni.amusementpark.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import hu.beni.amusementpark.entity.Visitor;

@Repository
public interface VisitorRepository extends JpaRepository<Visitor, Long> {

    @Query("Select count(v) from Visitor v where v.machine.id = :machineId")
    public Long countByMachineId(@Param("machineId") Long machineId);

    @Query("Select v from Visitor v where v.amusementPark.id = :amusementParkId and v.id = :visitorId")
    public Visitor findByAmusementParkIdAndVisitorId(@Param("amusementParkId") Long amusementParkId, @Param("visitorId") Long visitorId);

    @Query("Select v from Visitor v where v.machine.id = :machineId and v.id = :visitorId")
    public Visitor findByMachineIdAndVisitorId(@Param("machineId") Long machineId, @Param("visitorId") Long visitorId);

    @Query("Select new hu.beni.amusementpark.entity.Visitor(v.id) from Visitor v where v.amusementPark.id = :amusementParkId and v.id = :visitorId")
    public Visitor findByAmusementParkIdAndVisitorIdReadOnlyId(@Param("amusementParkId") Long amusementParkId, @Param("visitorId")Long visitorId);
    
}
