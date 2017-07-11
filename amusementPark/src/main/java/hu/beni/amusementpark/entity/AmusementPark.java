package hu.beni.amusementpark.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

@Entity
@Data
@Builder
public class AmusementPark implements Serializable {

    @Id
    @GeneratedValue
    @JsonProperty("identifier")
    private Long id;

    private String name;

    private Integer capital;

    private Integer totalArea;

    private Integer entranceFee;

    @Fetch(FetchMode.SELECT)
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
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
}
