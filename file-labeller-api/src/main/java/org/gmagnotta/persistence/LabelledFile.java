package org.gmagnotta.persistence;

import java.util.List;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.LockModeType;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

@Entity
public class LabelledFile extends PanacheEntityBase {

    @Id
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

    public static LabelledFile findByIdForUpdate(String name) {
        return LabelledFile.findById(name, LockModeType.PESSIMISTIC_WRITE);
    }

}
