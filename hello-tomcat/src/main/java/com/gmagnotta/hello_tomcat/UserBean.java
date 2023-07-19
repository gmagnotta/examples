package com.gmagnotta.hello_tomcat;

import java.io.IOException;
import java.io.Serializable;
import java.security.Principal;
import java.text.MessageFormat;
import java.util.ResourceBundle;

import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

@Named
@SessionScoped
public class UserBean implements Serializable {
    private String name;

    public UserBean() {
        this.name = "";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGreeting() {

        String tempUser;
        if (name.length() == 0) {

            ResourceBundle bundle = ResourceBundle.getBundle("messages",
                    FacesContext.getCurrentInstance().getViewRoot().getLocale());
            tempUser = bundle.getString("user");

        } else {

            tempUser = name;

        }

        ResourceBundle bundle = ResourceBundle.getBundle("messages",
                FacesContext.getCurrentInstance().getViewRoot().getLocale());
        String greet = bundle.getString("greet");

        return MessageFormat.format(greet, tempUser);

    }

    public boolean isLogged() {
        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext()
                .getRequest();

        return (request.getUserPrincipal() != null);
    }

    public String getUsername() {

        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext()
                .getRequest();

        Principal principal = request.getUserPrincipal();

        if (principal != null) {
            return principal.getName();
        }

        return "UNKNOWN";

    }

    public void logout() throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext()
                .getRequest();

        request.logout();

        // FacesContext.getCurrentInstance().getExternalContext().invalidateSession();

        // FacesContext.getCurrentInstance().getExternalContext().responseSendError(401,
        // "You are logged out.");

        FacesContext.getCurrentInstance().getExternalContext().setResponseStatus(401);
        FacesContext.getCurrentInstance().getExternalContext().getResponseOutputWriter()
                .write("<html><head><meta http-equiv='refresh' content='0;/index.xhtml'></head></html>");

        FacesContext.getCurrentInstance().responseComplete();
        // return "/index.xhtml?faces-redirect=true";
    }

}
