package org.gmagnotta.persistence;

import java.util.List;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

@Entity
public class LabelledFile extends PanacheEntity {

    String name;

    @ElementCollection
    List<String> labels;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getLabels() {
        return labels;
    }

    public void setLabels(List<String> labels) {
        this.labels = labels;
    }

    public void appendLabels(List<String> labels) {
        this.labels.addAll(labels);
    }

    public static LabelledFile findByName(String name) {
        if (name == null)
            return null;

        return find("name", name).firstResult();
    }

}
