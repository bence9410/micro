package hu.beni.clientsupport.dto;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ArchiveVisitorDTO implements Serializable {

	private static final long serialVersionUID = 7802032004601520550L;

	private String email;

	private LocalDate dateOfBirth;

	private LocalDateTime dateOfSignUp;

}
