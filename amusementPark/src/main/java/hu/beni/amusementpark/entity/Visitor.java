package hu.beni.amusementpark.entity;

import java.io.Serializable;
import java.sql.Timestamp;
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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import hu.beni.amusementpark.enums.VisitorState;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Tolerate;

@Entity
@Getter
@Setter
@Builder
@EqualsAndHashCode(of = {"id", "spendingMoney", "age", "dateOfEntry", "active", "state"})
public class Visitor implements Serializable {

    @Id
    @GeneratedValue
    @JsonProperty("identifier")
    private Long id;

    private Integer spendingMoney;

    private Integer age;

    private Timestamp dateOfEntry;
    
    private Boolean active;

    @Enumerated(EnumType.STRING)
    @Column(name = "Visitor_State")
    private VisitorState state;

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
    
    @Tolerate
    public Visitor(Long id) {
    	this.id = id;
    }

}
