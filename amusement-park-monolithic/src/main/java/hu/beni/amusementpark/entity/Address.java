package hu.beni.amusementpark.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Tolerate;

@Entity
@Data
@Builder
@EqualsAndHashCode(of = {"id", "zipCode", "country", "city", "street", "houseNumber"})
public class Address implements Serializable {

    private static final long serialVersionUID = 5753682920839496113L;

	@Id
    @Column(name = "id")
    @JsonProperty("identifier")
    private Long id;

    @NotNull
    @Size(min = 3, max = 15)
    private String country;

    @NotNull
    @Size(min = 3, max = 10)
    private String zipCode;
    
    @NotNull
    @Size(min = 3, max = 15)
    private String city;

    @NotNull
    @Size(min = 5, max = 25)
    private String street;

    @NotEmpty
    @Size(max = 5)
    private String houseNumber;
    
    @MapsId
    @JoinColumn(name = "id")
    @OneToOne(mappedBy = "address", fetch = FetchType.LAZY, optional = false)
    @JsonIgnore
    private AmusementPark amusementPark;

    @Tolerate
    protected Address() {
        super();
    }
}
