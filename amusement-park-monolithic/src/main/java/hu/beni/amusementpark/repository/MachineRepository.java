package hu.beni.amusementpark.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import hu.beni.amusementpark.entity.Machine;

public interface MachineRepository extends JpaRepository<Machine, Long> {

	@Query("Select Sum(m.size) from Machine m where m.amusementPark.id = :amusementParkId")
	Optional<Long> sumAreaByAmusementParkId(Long amusementParkId);

	@Query("Select m from Machine m where m.amusementPark.id = :amusementParkId and m.id = :machineId")
	Optional<Machine> findByAmusementParkIdAndMachineId(Long amusementParkId, Long machineId);

	@Query("Select m from Machine m where m.amusementPark.id = :amusementParkId")
	List<Machine> findAllByAmusementParkId(Long amusementParkId);

}
