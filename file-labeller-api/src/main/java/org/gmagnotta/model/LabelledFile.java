package org.gmagnotta.model;

import java.util.List;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class LabelledFile {

    public String name;
    public List<String> labels;
    
}
