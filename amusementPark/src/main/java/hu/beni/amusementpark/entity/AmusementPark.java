package hu.beni.amusementpark.entity;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Tolerate;

@Entity
@Getter
@Setter
@Builder
@EqualsAndHashCode(of = {"id", "name", "capital", "totalArea", "entranceFee"})
public class AmusementPark implements Serializable {

    @Id
    @GeneratedValue
    @JsonProperty("identifier")
    private Long id;

    private String name;

    private Integer capital;

    private Integer totalArea;

    private Integer entranceFee;
        
    @NotNull
    @PrimaryKeyJoinColumn
    @OneToOne(cascade = CascadeType.ALL)
    private Address address;

    @OneToMany(mappedBy = "amusementPark", cascade = CascadeType.REMOVE)
    @JsonIgnore
    private List<GuestBookRegistry> guestBookRegistry;

    @OneToMany(mappedBy = "amusementPark", cascade = CascadeType.REMOVE)
    @JsonIgnore
    private List<Machine> machines;

    @OneToMany(mappedBy = "amusementPark", cascade = CascadeType.REMOVE)
    @JsonIgnore
    private List<Visitor> activeVisitors;

    @ManyToMany
    private Set<Visitor> visitors;
    
    @Tolerate
    protected AmusementPark() {
        super();
    }
    
    @Tolerate
    public AmusementPark(Long id) {
    	this.id = id;
    }

    @Tolerate
    public AmusementPark(Long id, Integer entranceFee) {
        this.id = id;
        this.entranceFee = entranceFee;
    }

    @Tolerate
    public AmusementPark(Long id, Integer capital, Integer totalArea) {
        this.id = id;
        this.capital = capital;
        this.totalArea = totalArea;
    }
}
