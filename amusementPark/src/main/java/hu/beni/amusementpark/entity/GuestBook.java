package hu.beni.amusementpark.entity;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

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
@EqualsAndHashCode(of = {"id", "textOfRegistry", "dateOfRegistry"})
public class GuestBook implements Serializable {

    @Id
    @GeneratedValue
    @JsonProperty("identifier")
    private Long id;

    private String textOfRegistry;

    private Timestamp dateOfRegistry;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    private AmusementPark amusementPark;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    private Visitor visitor;

    @Tolerate
    protected GuestBook() {
        super();
    }

}
