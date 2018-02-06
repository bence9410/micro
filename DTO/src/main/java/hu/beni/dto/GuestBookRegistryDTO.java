package hu.beni.dto;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GuestBookRegistryDTO {
	
	private Long identifier;
	
	private String textOfRegistry;
	
	private LocalDateTime dateOfRegistry;
	
	private Long visitorId;
	
}
