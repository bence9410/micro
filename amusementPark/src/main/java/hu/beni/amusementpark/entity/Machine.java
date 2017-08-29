package hu.beni.amusementpark.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import hu.beni.amusementpark.enums.MachineType;
import java.util.List;
import javax.persistence.OneToMany;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Tolerate;

@Entity
@Getter
@Setter
@Builder
@EqualsAndHashCode(of = {"id", "fantasyName", "size", "price", "numberOfSeats", "minimumRequiredAge", "ticketPrice", "type"})
public class Machine implements Serializable {

    @Id
    @GeneratedValue
    @JsonProperty("identifier")
    private Long id;

    private String fantasyName;

    @Column(name = "Size_Of_Machine")
    private Integer size;

    private Integer price;

    private Integer numberOfSeats;

    private Integer minimumRequiredAge;

    private Integer ticketPrice;

    @Enumerated(EnumType.STRING)
    private MachineType type;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    private AmusementPark amusementPark;

    @JsonIgnore
    @OneToMany(mappedBy = "machine")
    private List<Visitor> visitors;

    @Tolerate
    protected Machine() {
        super();
    }

}
