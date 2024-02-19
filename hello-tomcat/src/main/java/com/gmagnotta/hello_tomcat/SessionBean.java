package com.gmagnotta.hello_tomcat;

import java.io.Serializable;
import java.util.ResourceBundle;

import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;

@Named
@SessionScoped
public class SessionBean implements Serializable {
    
    private String value;

    public SessionBean() {
        this.value = "";
    }

    public String getValue() {

        return value;

    }

    public String toString() {

        if (value.length() == 0) {

            ResourceBundle bundle = ResourceBundle.getBundle("messages",
                    FacesContext.getCurrentInstance().getViewRoot().getLocale());
            
            return bundle.getString("empty_session");

        } else {

            return value;

        }

    }

    public void setValue(String value) {
        this.value = value;
    }

}
