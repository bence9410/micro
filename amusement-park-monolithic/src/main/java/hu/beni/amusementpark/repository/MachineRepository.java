package hu.beni.amusementpark.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import static hu.beni.amusementpark.constants.ParameterMappingConstants.*;

import java.util.List;
import java.util.Optional;

import hu.beni.amusementpark.entity.Machine;

@Repository
public interface MachineRepository extends JpaRepository<Machine, Long> {

    @Query("Select Sum(m.size) from Machine m where m.amusementPark.id = :amusementParkId")
    public Optional<Long> sumAreaByAmusementParkId(@Param(AMUSEMENT_PARK_ID) Long amusementParkId);

    @Query("Select m from Machine m where m.amusementPark.id = :amusementParkId and m.id = :machineId")
    public Optional<Machine> findByAmusementParkIdAndMachineId(@Param(AMUSEMENT_PARK_ID) Long amusementParkId, @Param(MACHINE_ID) Long machineId);

    @Query("Select m from Machine m where m.amusementPark.id = :amusementParkId")
    public List<Machine> findAllByAmusementParkId(@Param(AMUSEMENT_PARK_ID) Long amusementParkId);
    
}
