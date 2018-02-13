package hu.beni.amusementpark.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.EntityGraph.EntityGraphType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import static hu.beni.amusementpark.constants.ParameterMappingConstants.*;

import java.util.Optional;

import hu.beni.amusementpark.entity.AmusementPark;

@Repository
public interface AmusementParkRepository extends JpaRepository<AmusementPark, Long>, JpaSpecificationExecutor<AmusementPark>{

	@Query("Select a from AmusementPark a where a.id = :amusementParkId")
	@EntityGraph(attributePaths = "address", type = EntityGraphType.FETCH)
	public Optional<AmusementPark> findByIdFetchAddress(@Param(AMUSEMENT_PARK_ID) Long amusementParkId);
	
	@Query("Select a from AmusementPark a")
	@EntityGraph(attributePaths = "address", type = EntityGraphType.FETCH)
	public Page<AmusementPark> findAllFetchAddress(Pageable pageable);
	
    @Modifying
    @Query("Update AmusementPark a set a.capital = a.capital - :ammount where a.id = :amusementParkId")
    public void decreaseCapitalById(@Param(AMMOUNT) Integer ammount, @Param(AMUSEMENT_PARK_ID) Long amusementParkId);

    @Modifying
    @Query("Update AmusementPark a set a.capital = a.capital + :ammount where a.id = :amusementParkId")
    public void incrementCapitalById(@Param(AMMOUNT) Integer ammount, @Param(AMUSEMENT_PARK_ID) Long amusementParkId);

    @Query("Select new hu.beni.amusementpark.entity.AmusementPark(a.id) from AmusementPark a where a.id = :amusementParkId")
    public Optional<AmusementPark> findByIdReadOnlyId(@Param(AMUSEMENT_PARK_ID) Long amusementParkId);
    
    @Query("Select new hu.beni.amusementpark.entity.AmusementPark(a.id, a.entranceFee) from AmusementPark a where a.id = :amusementParkId")
    public Optional<AmusementPark> findByIdReadOnlyIdAndEntranceFee(@Param(AMUSEMENT_PARK_ID) Long amusementParkId);
    
    @Query("Select new hu.beni.amusementpark.entity.AmusementPark(a.id, a.capital, a.totalArea) from AmusementPark a where a.id = :amusementParkId")
    public Optional<AmusementPark> findByIdReadOnlyIdAndCapitalAndTotalArea(@Param(AMUSEMENT_PARK_ID) Long amusementParkId);

    @Query(nativeQuery = true, value = "Select count(*) from amusement_park_visitor where amusement_park_id = :amusementParkId and visitor_id = :visitorId")
    public Long countKnownVisitor(@Param(AMUSEMENT_PARK_ID) Long amusementParkId, @Param(VISITOR_ID) Long visitorId);

    @Modifying
    @Query(nativeQuery = true, value = "Insert into amusement_park_visitor(amusement_park_id, visitor_id) values(:amusementParkId, :visitorId)")
    public void addKnownVisitor(@Param(AMUSEMENT_PARK_ID) Long amusementParkId, @Param(VISITOR_ID) Long visitorId);
    
}
