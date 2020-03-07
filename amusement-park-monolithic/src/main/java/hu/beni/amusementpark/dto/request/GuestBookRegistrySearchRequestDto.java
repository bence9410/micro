package hu.beni.amusementpark.dto.request;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Data
public class GuestBookRegistrySearchRequestDto {

	@JsonIgnore
	private Long amusementParkId;

	private String visitorEmail;

	private String textOfRegistry;

	private LocalDateTime dateOfRegistryMin;

	private LocalDateTime dateOfRegistryMax;

}
