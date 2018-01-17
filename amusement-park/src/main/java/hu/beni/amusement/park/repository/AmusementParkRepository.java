package hu.beni.amusement.park.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import hu.beni.amusement.park.entity.AmusementPark;

import static hu.beni.amusement.park.constants.ParameterMappingConstants.*;

import java.util.List;

@Repository
public interface AmusementParkRepository extends JpaRepository<AmusementPark, Long>, JpaSpecificationExecutor<AmusementPark>{

	@Query("Select a from AmusementPark a join fetch a.address")
	public List<AmusementPark> findAllFetchAddress();
	
    @Modifying
    @Query("Update AmusementPark a set a.capital = a.capital - :ammount where a.id = :amusementParkId")
    public void decreaseCapitalById(@Param(AMMOUNT) Integer ammount, @Param(AMUSEMENT_PARK_ID) Long amusementParkId);

    @Modifying
    @Query("Update AmusementPark a set a.capital = a.capital + :ammount where a.id = :amusementParkId")
    public void incrementCapitalById(@Param(AMMOUNT) Integer ammount, @Param(AMUSEMENT_PARK_ID) Long amusementParkId);

    @Query("Select new hu.beni.amusement.park.entity.AmusementPark(a.id) from AmusementPark a where a.id = :amusementParkId")
    public AmusementPark findByIdReadOnlyId(@Param(AMUSEMENT_PARK_ID) Long amusementParkId);
    
    @Query("Select new hu.beni.amusement.park.entity.AmusementPark(a.id, a.entranceFee) from AmusementPark a where a.id = :amusementParkId")
    public AmusementPark findByIdReadOnlyIdAndEntranceFee(@Param(AMUSEMENT_PARK_ID) Long amusementParkId);
    
    @Query("Select new hu.beni.amusement.park.entity.AmusementPark(a.id, a.capital, a.totalArea) from AmusementPark a where a.id = :amusementParkId")
    public AmusementPark findByIdReadOnlyIdAndCapitalAndTotalArea(@Param(AMUSEMENT_PARK_ID) Long amusementParkId);

    @Query(nativeQuery = true, value = "Select count(*) from amusement_park_visitor where amusement_park_id = :amusementParkId and visitor_id = :visitorId")
    public Long countKnownVisitor(@Param(AMUSEMENT_PARK_ID) Long amusementParkId, @Param(VISITOR_ID) Long visitorId);

    @Modifying
    @Query(nativeQuery = true, value = "Insert into amusement_park_visitor(amusement_park_id, visitor_id) values(:amusementParkId, :visitorId)")
    public void addKnownVisitor(@Param(AMUSEMENT_PARK_ID) Long amusementParkId, @Param(VISITOR_ID) Long visitorId);
    
}
