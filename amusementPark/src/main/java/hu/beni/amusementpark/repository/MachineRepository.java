package hu.beni.amusementpark.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import hu.beni.amusementpark.entity.Machine;

@Repository
public interface MachineRepository extends JpaRepository<Machine, Long> {

    @Query("Select Sum(m.size) from Machine m where m.amusementPark.id = :amusementParkId")
    public Long sumAreaByAmusementParkId(@Param("amusementParkId") Long amusementParkId);

    @Query("Select m from Machine m where m.amusementPark.id = :amusementParkId and m.id = :machineId")
    public Machine findByAmusementParkIdAndMachineId(@Param("amusementParkId") Long amusementParkId, @Param("machineId") Long machineId);

}
