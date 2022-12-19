package org.gmagnotta.model;

import java.util.List;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class LabelledFile {

    @NotNull
    public String name;

    @NotEmpty
    @NotNull
    public List<String> labels;
    
}
