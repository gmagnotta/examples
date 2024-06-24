
 package org.gmagnotta.model;


public class Member {

    public Long id;

    private String name;
    private String email;
    private String rank;

    public Long battalion;

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

    public Long getBattalion() {
        return battalion;
    }

    public void setBattalion(Long battalion) {
        this.battalion = battalion;
    }

    

}
