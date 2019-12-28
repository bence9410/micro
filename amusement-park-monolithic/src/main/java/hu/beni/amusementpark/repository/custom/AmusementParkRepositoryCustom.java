package hu.beni.amusementpark.repository.custom;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import hu.beni.amusementpark.dto.request.AmusementParkSearchRequestDto;
import hu.beni.amusementpark.dto.response.AmusementParkPageResponseDto;

public interface AmusementParkRepositoryCustom {

	Page<AmusementParkPageResponseDto> findAll(AmusementParkSearchRequestDto dto, Pageable pageable);

}
