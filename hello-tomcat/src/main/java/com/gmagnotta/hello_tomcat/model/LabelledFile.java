package com.gmagnotta.hello_tomcat.model;

import java.util.List;

public class LabelledFile {

    public String name;
    public List<String> labels;

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

}
