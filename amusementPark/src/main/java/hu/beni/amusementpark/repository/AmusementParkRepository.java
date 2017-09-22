package hu.beni.amusementpark.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import static hu.beni.amusementpark.constants.ParameterMappingConstants.*;
import hu.beni.amusementpark.entity.AmusementPark;

@Repository
public interface AmusementParkRepository extends JpaRepository<AmusementPark, Long>, JpaSpecificationExecutor<AmusementPark>{

    @Modifying
    @Query("Update AmusementPark a set a.capital = a.capital - :ammount where a.id = :amusementParkId")
    public void decreaseCapitalById(@Param(AMMOUNT) Integer ammount, @Param(AMUSEMENT_PARK_ID) Long amusementParkId);

    @Modifying
    @Query("Update AmusementPark a set a.capital = a.capital + :ammount where a.id = :amusementParkId")
    public void incrementCapitalById(@Param(AMMOUNT) Integer ammount, @Param(AMUSEMENT_PARK_ID) Long amusementParkId);

    @Query("Select new hu.beni.amusementpark.entity.AmusementPark(a.id) from AmusementPark a where a.id = :amusementParkId")
    public AmusementPark findByIdReadOnlyId(@Param(AMUSEMENT_PARK_ID) Long amusementParkId);
    
    @Query("Select new hu.beni.amusementpark.entity.AmusementPark(a.id, a.entranceFee) from AmusementPark a where a.id = :amusementParkId")
    public AmusementPark findByIdReadOnlyIdAndEntranceFee(@Param(AMUSEMENT_PARK_ID) Long amusementParkId);
    
    @Query("Select new hu.beni.amusementpark.entity.AmusementPark(a.id, a.capital, a.totalArea) from AmusementPark a where a.id = :amusementParkId")
    public AmusementPark findByIdReadOnlyIdAndCapitalAndTotalArea(@Param(AMUSEMENT_PARK_ID) Long amusementParkId);
    
}
