package hu.beni.amusementpark.repository.custom;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import hu.beni.amusementpark.dto.request.MachineSearchRequestDto;
import hu.beni.amusementpark.dto.response.MachineSearchResponseDto;

public interface MachineRepositoryCustom {

	Page<MachineSearchResponseDto> findAll(MachineSearchRequestDto dto, Pageable pageable);

}
