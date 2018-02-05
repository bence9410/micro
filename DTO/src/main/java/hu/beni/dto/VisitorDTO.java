package hu.beni.dto;

import java.time.LocalDate;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;

@Data
@Builder
public class VisitorDTO {
	
    private Long identifier;
    
    private String name;
    
    private LocalDate dateOfBirth;

	private Integer spendingMoney;
	
    private String state;
		  
    @Tolerate
    protected VisitorDTO() {
        super();
    }
}
