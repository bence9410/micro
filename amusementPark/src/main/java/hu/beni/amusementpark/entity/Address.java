package hu.beni.amusementpark.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;

@Entity
@Data
@Builder
public class Address implements Serializable {

    @Id
    @GeneratedValue
    @JsonProperty("identifier")
    private Long id;

    private Integer zipCode;

    private String country;

    private String city;

    private String street;

    private String houseNumber;

    @Tolerate
    protected Address() {
        super();
    }

}
