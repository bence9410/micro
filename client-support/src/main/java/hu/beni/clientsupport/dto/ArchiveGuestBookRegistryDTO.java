package hu.beni.clientsupport.dto;

import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ArchiveGuestBookRegistryDTO implements Serializable {

	private static final long serialVersionUID = -1105801300334244374L;

	private Long identifier;

	private String textOfRegistry;

	private LocalDateTime dateOfRegistry;

	private Long visitorId;

}
