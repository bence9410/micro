package hu.beni.amusementpark.repository;

import static hu.beni.amusementpark.constants.ParameterMappingConstants.AMMOUNT;
import static hu.beni.amusementpark.constants.ParameterMappingConstants.AMUSEMENT_PARK_ID;
import static hu.beni.amusementpark.constants.ParameterMappingConstants.VISITOR_ID;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import hu.beni.amusementpark.entity.AmusementPark;

public interface AmusementParkRepository
		extends JpaRepository<AmusementPark, Long>, JpaSpecificationExecutor<AmusementPark> {

	@Query("Select a from AmusementPark a join fetch a.address where a.id = :amusementParkId")
	Optional<AmusementPark> findByIdFetchAddress(@Param(AMUSEMENT_PARK_ID) Long amusementParkId);

	@Query("Select a from AmusementPark a")
	@EntityGraph(attributePaths = "address")
	Page<AmusementPark> findAllFetchAddress(Pageable pageable);

	@Modifying
	@Query("Update AmusementPark a set a.capital = a.capital - :ammount where a.id = :amusementParkId")
	void decreaseCapitalById(@Param(AMMOUNT) Integer ammount, @Param(AMUSEMENT_PARK_ID) Long amusementParkId);

	@Modifying
	@Query("Update AmusementPark a set a.capital = a.capital + :ammount where a.id = :amusementParkId")
	void incrementCapitalById(@Param(AMMOUNT) Integer ammount, @Param(AMUSEMENT_PARK_ID) Long amusementParkId);

	@Query("Select new hu.beni.amusementpark.entity.AmusementPark(a.id) from AmusementPark a where a.id = :amusementParkId")
	Optional<AmusementPark> findByIdReadOnlyId(@Param(AMUSEMENT_PARK_ID) Long amusementParkId);

	@Query("Select new hu.beni.amusementpark.entity.AmusementPark(a.id, a.entranceFee) from AmusementPark a where a.id = :amusementParkId")
	Optional<AmusementPark> findByIdReadOnlyIdAndEntranceFee(@Param(AMUSEMENT_PARK_ID) Long amusementParkId);

	@Query("Select new hu.beni.amusementpark.entity.AmusementPark(a.id, a.capital, a.totalArea) from AmusementPark a where a.id = :amusementParkId")
	Optional<AmusementPark> findByIdReadOnlyIdAndCapitalAndTotalArea(@Param(AMUSEMENT_PARK_ID) Long amusementParkId);

	@Query(nativeQuery = true, value = "Select count(*) from amusement_park_visitor where amusement_park_id = :amusementParkId and visitor_id = :visitorId")
	Long countKnownVisitor(@Param(AMUSEMENT_PARK_ID) Long amusementParkId, @Param(VISITOR_ID) Long visitorId);

	@Modifying
	@Query(nativeQuery = true, value = "Insert into amusement_park_visitor(amusement_park_id, visitor_id) values(:amusementParkId, :visitorId)")
	void addKnownVisitor(@Param(AMUSEMENT_PARK_ID) Long amusementParkId, @Param(VISITOR_ID) Long visitorId);

}
