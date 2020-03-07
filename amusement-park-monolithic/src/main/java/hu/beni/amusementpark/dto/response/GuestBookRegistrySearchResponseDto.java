package hu.beni.amusementpark.dto.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class GuestBookRegistrySearchResponseDto {

	private String visitorEmail;

	private String textOfRegistry;

	private LocalDateTime dateOfRegistry;

}
