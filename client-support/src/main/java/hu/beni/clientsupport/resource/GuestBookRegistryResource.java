package hu.beni.clientsupport.resource;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.ResourceSupport;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class GuestBookRegistryResource extends ResourceSupport implements Serializable {

	private static final long serialVersionUID = -5588641314911131702L;

	private Long identifier;

	private String textOfRegistry;

	private LocalDateTime dateOfRegistry;

	private Long visitorId;

	@Builder
	public GuestBookRegistryResource(Long identifier, String textOfRegistry, LocalDateTime dateOfRegistry,
			Long visitorId, Link[] links) {
		super();
		this.identifier = identifier;
		this.textOfRegistry = textOfRegistry;
		this.dateOfRegistry = dateOfRegistry;
		this.visitorId = visitorId;
		Optional.ofNullable(links).ifPresent(this::add);
	}
}
