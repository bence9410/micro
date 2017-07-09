package hu.beni.amusementpark.service;

import org.springframework.stereotype.Service;

import hu.beni.amusementpark.entity.AmusementPark;
import hu.beni.amusementpark.repository.AmusementParkRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AmusementParkService {

    private final AmusementParkRepository amusementParkRepository;

    public AmusementPark create(AmusementPark amusementPark) {
        return amusementParkRepository.save(amusementPark);
    }

    public AmusementPark read(Long id) {
        return amusementParkRepository.findOne(id);
    }

    public void delete(Long id) {
        amusementParkRepository.delete(id);
    }
}
