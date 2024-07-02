
 package org.gmagnotta.dto;

import jakarta.json.bind.annotation.JsonbTransient;

public class Member {

    private Long id;

    private String name;
    private String email;
    private String rank;

    @JsonbTransient
    private Battalion battalion;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public Battalion getBattalion() {
        return battalion;
    }

    public void setBattalion(Battalion battalion) {
        this.battalion = battalion;
    }

    

}
