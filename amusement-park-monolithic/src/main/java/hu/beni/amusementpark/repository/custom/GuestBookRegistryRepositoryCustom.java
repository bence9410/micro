package hu.beni.amusementpark.repository.custom;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import hu.beni.amusementpark.dto.request.GuestBookRegistryRequestDto;
import hu.beni.amusementpark.dto.response.GuestBookRegistryResponseDto;

public interface GuestBookRegistryRepositoryCustom {

	Page<GuestBookRegistryResponseDto> findAll(GuestBookRegistryRequestDto dto, Pageable pageable);

}
