package hu.beni.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ArchiveVisitorDTO {

	private Long identifier;

	private String name;

	private String username;

	private LocalDate dateOfBirth;

	private LocalDateTime dateOfSignUp;

	private String state;

}
