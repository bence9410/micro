package hu.beni.tester.dto;

import java.time.LocalDate;

import hu.beni.tester.enums.VisitorState;
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
	
    private VisitorState state;
		  
    @Tolerate
    protected VisitorDTO() {
        super();
    }
}
