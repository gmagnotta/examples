package org.gmagnotta.model;

import jakarta.json.bind.annotation.JsonbTransient;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

@Entity
@Table(name="Equipment")
@NamedQuery(name = "Equipment.findAll", query = "SELECT e FROM Equipment e")
public class Equipment {
    
    @Id
    @GeneratedValue(generator = "equipmentSequence")
    @SequenceGenerator(name = "equipmentSequence", allocationSize = 50, initialValue = 100)
    public Long id;

    private String name;
    private String type;
    private String code;
    private String status;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JoinColumn(name = "battalion")
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
