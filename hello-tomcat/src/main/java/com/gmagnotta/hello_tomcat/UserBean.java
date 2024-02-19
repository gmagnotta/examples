package com.gmagnotta.hello_tomcat;

import java.io.IOException;
import java.io.Serializable;
import java.security.Principal;

import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;

@Named
@SessionScoped
public class UserBean implements Serializable {

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

        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();

        // FacesContext.getCurrentInstance().getExternalContext().responseSendError(401,
        // "You are logged out.");

        FacesContext.getCurrentInstance().getExternalContext().setResponseStatus(401);
        FacesContext.getCurrentInstance().getExternalContext().getResponseOutputWriter()
                .write("<html><head><meta http-equiv='refresh' content='0;/index.xhtml'></head></html>");

        FacesContext.getCurrentInstance().responseComplete();
        // return "/index.xhtml?faces-redirect=true";
    }

}
