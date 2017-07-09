package hu.beni.amusementpark.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Calendar;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import hu.beni.amusementpark.enums.VisitorState;
import java.util.List;
import javax.persistence.OneToMany;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;

@Entity
@Data
@Builder
public class Visitor implements Serializable {

    @Id
    @GeneratedValue
    @JsonProperty("identifier")
    private Long id;

    private Integer spendingMoney;

    private Integer age;

    @Builder.Default
    private Timestamp dateOfEntry = Timestamp.from(Calendar.getInstance().toInstant());

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "Visitor_State")
    private VisitorState state = VisitorState.REST;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    private AmusementPark amusementPark;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    private Machine machine;

    @JsonIgnore
    @OneToMany(mappedBy = "visitor")
    private List<GuestBook> guestBooks;

    @Tolerate
    protected Visitor() {
        super();
    }

}
