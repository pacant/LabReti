import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties({"status"})
public class User {
    private Boolean status; // true ONLINE, false OFFLINE
    // credenziali di accesso
    private String nick;
    private String pass;

    public User(String nick, String pass){
        this.nick=nick;
        this.pass=pass;
        this.status=false;
    }
    public User(){} // costruttore vuoto per serializzazione


    // getters and setters


    public void setStatus(Boolean status){
        this.status=status;
    }

    public boolean getStatus(){
        return this.status;
    }

    public void setNick(String nick){
        this.nick=nick;
    }

    public String getNick(){
        return this.nick;
    }

    public void setPass(String pass){
        this.pass=pass;
    }

    public String getPass(){
        return this.pass;
    }
}
