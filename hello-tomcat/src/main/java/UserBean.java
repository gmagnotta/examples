import java.io.Serializable;
import java.text.MessageFormat;
import java.util.ResourceBundle;

import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;

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
        if (name.length() == 0) {
            return "";
        } else {
            
            ResourceBundle bundle = ResourceBundle.getBundle("messages", FacesContext.getCurrentInstance().getViewRoot().getLocale());
            String greet = bundle.getString("greet");

            return MessageFormat.format(greet, name);

        }
    }
}
