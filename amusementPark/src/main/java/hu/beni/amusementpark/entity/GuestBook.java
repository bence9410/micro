package hu.beni.amusementpark.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Calendar;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;

@Entity
@Data
@Builder
public class GuestBook implements Serializable {

    @Id
    @GeneratedValue
    @JsonProperty("identifier")
    private Long id;

    private String textOfRegistry;

    @Builder.Default
    private Timestamp dateOfRegistry = Timestamp.from(Calendar.getInstance().toInstant());

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
