package hu.beni.amusementpark.test.unit;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import hu.beni.amusementpark.archive.ArchiveSender;
import hu.beni.amusementpark.entity.AmusementPark;
import hu.beni.amusementpark.exception.AmusementParkException;
import hu.beni.amusementpark.repository.AmusementParkRepository;
import hu.beni.amusementpark.repository.VisitorRepository;
import hu.beni.amusementpark.service.AmusementParkService;
import hu.beni.amusementpark.service.impl.AmusementParkServiceImpl;

import static hu.beni.amusementpark.constants.ErrorMessageConstants.*;

public class AmusementParkServiceUnitTests {

    private AmusementParkRepository amusementParkRepository;
    private VisitorRepository visitorRepository;
    private ArchiveSender archiveSender;
    
    private AmusementParkService amusementParkService;

    @Before
    public void setUp() {
        amusementParkRepository = mock(AmusementParkRepository.class);
        visitorRepository = mock(VisitorRepository.class);
        archiveSender = mock(ArchiveSender.class);
        amusementParkService = new AmusementParkServiceImpl(amusementParkRepository, visitorRepository, archiveSender);
    }

    @After
    public void verifyNoMoreInteractionsOnMocks() {
        verifyNoMoreInteractions(amusementParkRepository, visitorRepository, archiveSender);
    }

    @Test
    public void savePositive() {
        AmusementPark amusementPark = AmusementPark.builder().build();

        when(amusementParkRepository.save(amusementPark)).thenReturn(amusementPark);
        
        assertEquals(amusementPark, amusementParkService.save(amusementPark));
        
        verify(amusementParkRepository).save(amusementPark);
    }
    
    @Test
    public void findOneByIdNegativeNoPark() {
        Long amusementParkId = 0L;

        assertThatThrownBy(() -> amusementParkService.findByIdFetchAddress(amusementParkId))
        	.isInstanceOf(AmusementParkException.class)
        	.hasMessage(NO_AMUSEMENT_PARK_WITH_ID);

        verify(amusementParkRepository).findByIdFetchAddress(amusementParkId);
    }

    @Test
    public void findOneByIdPositive() {
        AmusementPark amusementPark = AmusementPark.builder().id(0L).build();
        Long amusementParkId = amusementPark.getId();

        when(amusementParkRepository.findByIdFetchAddress(amusementParkId)).thenReturn(Optional.of(amusementPark));

        assertEquals(amusementPark, amusementParkService.findByIdFetchAddress(amusementParkId));

        verify(amusementParkRepository).findByIdFetchAddress(amusementParkId);
    }
    
    @Test
    public void findOneBySpecificationNegativeNoPark() {
    	Specification<AmusementPark> specification = (Root<AmusementPark> root, CriteriaQuery<?> query,
    			CriteriaBuilder cb) -> cb.equal(root.get("name"), "asd");
    			
    	assertThatThrownBy(() -> amusementParkService.findOne(specification))
    		.isInstanceOf(AmusementParkException.class)
    		.hasMessage(NO_AMUSEMENT_PARK_WITH_ID);
    	
    	verify(amusementParkRepository).findOne(specification);
    }
    
    @Test
    public void findOneBySpecificationPositive() {
    	AmusementPark amusementPark = AmusementPark.builder().build();
    	Specification<AmusementPark> specification = (Root<AmusementPark> root, CriteriaQuery<?> query,
    			CriteriaBuilder cb) -> cb.equal(root.get("name"), "asd");
    			
    	when(amusementParkRepository.findOne(specification)).thenReturn(Optional.of(amusementPark));
    	
    	assertEquals(amusementPark, amusementParkService.findOne(specification));
    	
    	verify(amusementParkRepository).findOne(specification);
    }

    @Test
    public void deleteNegativeVisitorsInPark() {
    	Long amusementParkId = 0L;
    	Long numberOfVisitorsInPark = 10L;
    	
    	when(visitorRepository.countByAmusementParkId(amusementParkId)).thenReturn(numberOfVisitorsInPark);
    	
    	assertThatThrownBy(() -> amusementParkService.delete(amusementParkId))
    		.isInstanceOf(AmusementParkException.class).hasMessage(VISITORS_IN_PARK);
    	
    	verify(visitorRepository).countByAmusementParkId(amusementParkId);
    }
    
    @Test
    public void deleteNegativeNoParkWithId() {
    	Long amusementParkId = 0L;
    	Long numberOfVisitorsInPark = 0L;
    	
    	when(visitorRepository.countByAmusementParkId(amusementParkId)).thenReturn(numberOfVisitorsInPark);
    	
    	assertThatThrownBy(() -> amusementParkService.delete(amusementParkId))
    		.isInstanceOf(AmusementParkException.class).hasMessage(NO_AMUSEMENT_PARK_WITH_ID);
    	
    	verify(visitorRepository).countByAmusementParkId(amusementParkId);
    	verify(amusementParkRepository).findById(amusementParkId);
    }
    
    @Test
    public void deletePositive() {
    	AmusementPark amusementPark = AmusementPark.builder().id(0L).build();
        Long amusementParkId = amusementPark.getId();
        Long numberOfVisitorsInPark = 0L;
        
        when(visitorRepository.countByAmusementParkId(amusementParkId)).thenReturn(numberOfVisitorsInPark);
        when(amusementParkRepository.findById(amusementParkId)).thenReturn(Optional.of(amusementPark));
        
        amusementParkService.delete(amusementParkId);

        verify(visitorRepository).countByAmusementParkId(amusementParkId);
        verify(amusementParkRepository).findById(amusementParkId);
        verify(amusementParkRepository).delete(amusementPark);
        verify(archiveSender).sendToArchive(amusementPark);
    }
    
    @Test
    public void findAllFetchAddressPositive() {
    	List<AmusementPark> amusementParks = Arrays.asList(
    			AmusementPark.builder().id(0L).build(),
    			AmusementPark.builder().id(1L).build());
    	
    	when(amusementParkRepository.findAllFetchAddress()).thenReturn(amusementParks);
    	
    	assertEquals(amusementParks, amusementParkService.findAllFetchAddress());
    	
    	verify(amusementParkRepository).findAllFetchAddress();
    }
    
    @Test
    public void findAllFetchAddressByPageablePositive() {
    	Page<AmusementPark> page = new PageImpl<>(Arrays.asList(
    			AmusementPark.builder().id(0L).build(),
    			AmusementPark.builder().id(1L).build()));
    	Pageable pageable = PageRequest.of(0, 10);
    	
    	when(amusementParkRepository.findAllFetchAddress(pageable)).thenReturn(page);
    	
    	assertEquals(page, amusementParkService.findAllFetchAddress(pageable));
    	
    	verify(amusementParkRepository).findAllFetchAddress(pageable);
    }
    
    @Test
    public void findAllBySpecificationPageable() {
    	List<AmusementPark> amusementParks = Arrays.asList(
    			AmusementPark.builder().id(0L).build(),
    			AmusementPark.builder().id(1L).build());
    	Specification<AmusementPark> specification = (Root<AmusementPark> root, CriteriaQuery<?> query,
    			CriteriaBuilder cb) -> cb.equal(root.get("name"), "asd");
    			
    	when(amusementParkRepository.findAll(specification)).thenReturn(amusementParks);
    	
    	assertEquals(amusementParks, amusementParkService.findAll(specification));
    	
    	verify(amusementParkRepository).findAll(specification);
    }
    
}