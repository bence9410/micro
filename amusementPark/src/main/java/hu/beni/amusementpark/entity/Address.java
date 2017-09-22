package hu.beni.amusementpark.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotEmpty;

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

    @NotNull
    @Size(min = 3, max = 10)
    private String zipCode;

    @NotNull
    @Size(min = 3, max = 15)
    private String country;

    @NotNull
    @Size(min = 3, max = 15)
    private String city;

    @NotNull
    @Size(min = 3, max = 25)
    private String street;

    @NotEmpty
    @Size(max = 5)
    private String houseNumber;

    @Tolerate
    protected Address() {
        super();
    }

}
