package hu.beni.dto;

import java.io.Serializable;
import java.time.LocalDate;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class VisitorDTO implements Serializable{
	
    private static final long serialVersionUID = -426306691990271010L;

	private Long identifier;
    
    private String name;
    
    private LocalDate dateOfBirth;

	private Integer spendingMoney;
	
    private String state;
		  
}
