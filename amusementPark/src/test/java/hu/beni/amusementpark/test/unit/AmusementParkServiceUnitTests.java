package hu.beni.amusementpark.test.unit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

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

import hu.beni.amusementpark.entity.AmusementPark;
import hu.beni.amusementpark.repository.AmusementParkRepository;
import hu.beni.amusementpark.service.AmusementParkService;
import hu.beni.amusementpark.service.impl.AmusementParkServiceImpl;

public class AmusementParkServiceUnitTests {

    private AmusementParkRepository amusementParkRepository;

    private AmusementParkService amusementParkService;

    @Before
    public void setUp() {
        amusementParkRepository = mock(AmusementParkRepository.class);
        amusementParkService = new AmusementParkServiceImpl(amusementParkRepository);
    }

    @After
    public void verifyNoMoreInteractionsOnMocks() {
        verifyNoMoreInteractions(amusementParkRepository);
    }

    @Test
    public void savePositive() {
        AmusementPark amusementPark = AmusementPark.builder().build();

        when(amusementParkRepository.save(amusementPark)).thenReturn(amusementPark);

        AmusementPark returnedAmusementPark = amusementParkService.save(amusementPark);
        
        assertEquals(amusementPark, returnedAmusementPark);
        
        verify(amusementParkRepository).save(amusementPark);
    }

    @Test
    public void findOneByIdPositive() {
        AmusementPark amusementPark = AmusementPark.builder().id(0L).build();
        Long amusementParkId = amusementPark.getId();

        when(amusementParkRepository.findOne(amusementParkId)).thenReturn(amusementPark);

        assertEquals(amusementPark, amusementParkService.findOne(amusementParkId));

        verify(amusementParkRepository).findOne(amusementParkId);
    }
    
    @Test
    public void findOneBySpecification() {
    	AmusementPark amusementPark = AmusementPark.builder().build();
    	Specification<AmusementPark> specification = (Root<AmusementPark> root, CriteriaQuery<?> query,
    			CriteriaBuilder cb) -> cb.equal(root.get("name"), "asd");
    			
    	when(amusementParkRepository.findOne(specification)).thenReturn(amusementPark);
    	
    	assertEquals(amusementPark, amusementParkService.findOne(specification));
    	
    	verify(amusementParkRepository).findOne(specification);
    }

    @Test
    public void deletePositive() {
        Long amusementParkId = 0L;

        amusementParkService.delete(amusementParkId);

        verify(amusementParkRepository).delete(amusementParkId);
    }
    
    @Test
    public void findAllByPageable() {
    	Page<AmusementPark> page = new PageImpl<>(Arrays.asList(
    			AmusementPark.builder().build(), AmusementPark.builder().build()));
    	Pageable pageable = new PageRequest(0, 10);
    	
    	when(amusementParkService.findAll(pageable)).thenReturn(page);
    	
    	assertEquals(page, amusementParkService.findAll(pageable));
    	
    	verify(amusementParkRepository).findAll(pageable);
    }
    
    @Test
    public void findAllBySpecification() {
    	List<AmusementPark> amusementParks = Arrays.asList(
    			AmusementPark.builder().build(), AmusementPark.builder().build());
    	Specification<AmusementPark> specification = (Root<AmusementPark> root, CriteriaQuery<?> query,
    			CriteriaBuilder cb) -> cb.equal(root.get("name"), "asd");
    			
    	when(amusementParkRepository.findAll(specification)).thenReturn(amusementParks);
    	
    	assertEquals(amusementParks, amusementParkService.findAll(specification));
    	
    	verify(amusementParkRepository).findAll(specification);
    }
    
}