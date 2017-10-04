package hu.beni.amusementpark.test.unit;

import hu.beni.amusementpark.entity.AmusementPark;
import hu.beni.amusementpark.repository.AmusementParkRepository;
import hu.beni.amusementpark.service.AmusementParkService;
import hu.beni.amusementpark.service.impl.AmusementParkServiceImpl;

import org.junit.After;
import org.junit.Before;
import static org.mockito.Mockito.*;
import static org.junit.Assert.*;
import org.junit.Test;

public class AmusementParkServiceTests {

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

        assertEquals(amusementPark, amusementParkService.save(amusementPark));

        verify(amusementParkRepository).save(amusementPark);
    }

    @Test
    public void findOnePositive() {
        AmusementPark amusementPark = AmusementPark.builder().id(0L).build();
        Long amusementParkId = amusementPark.getId();

        when(amusementParkRepository.findOne(amusementParkId)).thenReturn(amusementPark);

        assertEquals(amusementPark, amusementParkService.findOne(amusementParkId));

        verify(amusementParkRepository).findOne(amusementParkId);
    }

    @Test
    public void deletePositive() {
        Long amusementParkId = 0L;

        amusementParkService.delete(amusementParkId);

        verify(amusementParkRepository).delete(amusementParkId);
    }

}
