
 package org.gmagnotta.model;

import jakarta.json.bind.annotation.JsonbTransient;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

@Entity
@Table(name="Member")
@NamedQuery(name = "Members.findAll", query = "SELECT e FROM Member e")
public class Member {

    @Id
    @GeneratedValue(generator = "memberSequence")
    @SequenceGenerator(name = "memberSequence", allocationSize = 50, initialValue = 100)
    public Long id;

    private String name;
    @Column(unique=true)
    private String email;
    private String rank;

    @ManyToOne
    @JoinColumn(name = "battalion")
    @JsonbTransient 
    public Battalion battalion;

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }
    public String getRank() {
        return rank;
    }

}
