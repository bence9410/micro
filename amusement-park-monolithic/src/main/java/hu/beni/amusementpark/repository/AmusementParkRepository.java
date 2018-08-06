package hu.beni.amusementpark.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import hu.beni.amusementpark.entity.AmusementPark;

public interface AmusementParkRepository
		extends JpaRepository<AmusementPark, Long>, JpaSpecificationExecutor<AmusementPark> {

	@Query("Select a from AmusementPark a join fetch a.address where a.id = :amusementParkId")
	Optional<AmusementPark> findByIdFetchAddress(Long amusementParkId);

	@Query("Select a from AmusementPark a")
	@EntityGraph(attributePaths = "address")
	Page<AmusementPark> findAllFetchAddress(Pageable pageable);

	@Modifying
	@Query("Update AmusementPark a set a.capital = a.capital - :ammount where a.id = :amusementParkId")
	void decreaseCapitalById(Integer ammount, Long amusementParkId);

	@Modifying
	@Query("Update AmusementPark a set a.capital = a.capital + :ammount where a.id = :amusementParkId")
	void incrementCapitalById(Integer ammount, Long amusementParkId);

	@Query("Select new hu.beni.amusementpark.entity.AmusementPark(a.id) from AmusementPark a where a.id = :amusementParkId")
	Optional<AmusementPark> findByIdReadOnlyId(Long amusementParkId);

	@Query("Select new hu.beni.amusementpark.entity.AmusementPark(a.id, a.entranceFee) from AmusementPark a where a.id = :amusementParkId")
	Optional<AmusementPark> findByIdReadOnlyIdAndEntranceFee(Long amusementParkId);

	@Query("Select new hu.beni.amusementpark.entity.AmusementPark(a.id, a.capital, a.totalArea) from AmusementPark a where a.id = :amusementParkId")
	Optional<AmusementPark> findByIdReadOnlyIdAndCapitalAndTotalArea(Long amusementParkId);

	@Query(nativeQuery = true, value = "Select count(*) from amusement_park_visitor where amusement_park_id = :amusementParkId and visitor_id = :visitorId")
	Long countKnownVisitor(Long amusementParkId, Long visitorId);

	@Modifying
	@Query(nativeQuery = true, value = "Insert into amusement_park_visitor(amusement_park_id, visitor_id) values(:amusementParkId, :visitorId)")
	void addKnownVisitor(Long amusementParkId, Long visitorId);

}
