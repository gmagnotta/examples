
 package org.gmagnotta.model;

import jakarta.json.bind.annotation.JsonbTransient;


public class Member {

    public Long id;

    private String name;
    private String email;
    private String rank;

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
