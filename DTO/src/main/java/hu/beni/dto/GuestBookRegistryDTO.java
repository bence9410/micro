package hu.beni.dto;

import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GuestBookRegistryDTO implements Serializable{
	
	private static final long serialVersionUID = -5588641314911131702L;

	private Long identifier;
	
	private String textOfRegistry;
	
	private LocalDateTime dateOfRegistry;
	
	private Long visitorId;
	
}
