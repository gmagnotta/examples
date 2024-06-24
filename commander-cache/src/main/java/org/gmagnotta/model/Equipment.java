package org.gmagnotta.model;

import jakarta.json.bind.annotation.JsonbTransient;

public class Equipment {
    
    public Long id;

    private String name;
    private String type;
    private String code;
    private String status;

    @JsonbTransient
    private Battalion battalion;

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

    public Battalion getBattalion() {
        return battalion;
    }

    public void setBattalion(Battalion battalion) {
        this.battalion = battalion;
    }
    
}
