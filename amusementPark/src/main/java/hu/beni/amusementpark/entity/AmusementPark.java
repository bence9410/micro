package hu.beni.amusementpark.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Tolerate;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

@Entity
@Getter
@Setter
@Builder
@EqualsAndHashCode(of = {"id", "name", "capital", "totalArea", "entranceFee", "address"})
@ToString(of = {"id", "name", "capital", "totalArea", "entranceFee", "address"})
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
    @Fetch(FetchMode.SELECT)
    @OneToOne(cascade = CascadeType.ALL)
    private Address address;

    @JsonIgnore
    @OneToMany(mappedBy = "amusementPark")
    private List<Machine> machines;

    @JsonIgnore
    @OneToMany(mappedBy = "amusementPark")
    private List<Visitor> visitors;

    @JsonIgnore
    @OneToMany(mappedBy = "amusementPark")
    private List<GuestBook> guestBooks;

    @Tolerate
    protected AmusementPark() {
        super();
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
