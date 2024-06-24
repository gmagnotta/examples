package org.gmagnotta.model;


public class Equipment {
    
    public Long id;

    private String name;
    private String type;
    private String code;
    private String status;

    private Long battalion;

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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getBattalion() {
        return battalion;
    }

    public void setBattalion(Long battalion) {
        this.battalion = battalion;
    }

}
