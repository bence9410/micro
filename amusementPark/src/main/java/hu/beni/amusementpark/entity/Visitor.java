package hu.beni.amusementpark.entity;

import java.io.Serializable;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import hu.beni.amusementpark.enums.VisitorState;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Tolerate;

@Entity
@Getter
@Setter
@Builder
@EqualsAndHashCode(of = {"id", "name", "dateOfBirth", "dateOfRegistrate", "spendingMoney", "state"})
public class Visitor implements Serializable {

    @Id
    @GeneratedValue
    @JsonProperty("identifier")
    private Long id;
    
    private String name;

    private LocalDate dateOfBirth;

    @CreationTimestamp
    private Timestamp dateOfRegistrate;   
    
	private Integer spendingMoney;
	
    @Enumerated(EnumType.STRING)
    @Column(name = "Visitor_State")
    private VisitorState state;
		
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    private AmusementPark amusementPark;

	@ManyToOne(fetch = FetchType.LAZY)
	@JsonIgnore
    private Machine machine;
	    
    @OneToMany(mappedBy = "visitor")
    @JsonIgnore
    private List<GuestBookRegistry> guestBookRegistries;
    
    @ManyToMany(mappedBy = "visitors")
    @JsonIgnore
    private Set<AmusementPark> visitedAmusementParks;
  
    @Tolerate
    protected Visitor() {
        super();
    }
    
    @Tolerate
    public Visitor(Long id) {
    	this.id = id;
    }

}
