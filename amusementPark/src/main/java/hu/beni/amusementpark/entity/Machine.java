package hu.beni.amusementpark.entity;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.Range;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import hu.beni.amusementpark.enums.MachineType;
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

    @NotNull
    @Size(min = 5, max = 25)
    private String fantasyName;

    @NotNull
    @Range(min = 20, max = 200)
    @Column(name = "Size_Of_Machine")
    private Integer size;

    @NotNull
    @Range(min = 50, max = 2000)
    private Integer price;

    @NotNull
    @Range(min = 5, max = 30)
    private Integer numberOfSeats;

    @NotNull
    @Range(max = 21)
    private Integer minimumRequiredAge;

    @NotNull
    @Range(min = 5, max = 30)
    private Integer ticketPrice;

    @NotNull
    @Enumerated(EnumType.STRING)
    private MachineType type;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JsonIgnore
    private AmusementPark amusementPark;

    @OneToMany(mappedBy = "machine")
    @JsonIgnore
    private List<Visitor> visitors;

    @Tolerate
    protected Machine() {
        super();
    }

}
