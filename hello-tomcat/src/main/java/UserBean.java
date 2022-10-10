import java.io.Serializable;

import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

@Named("user")
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
            return "Hello, " + name + "!";
        }
    }
}
