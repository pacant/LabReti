import java.io.Serializable;
import java.util.List;
import java.util.Map;

// oggetti di risposta ad un comando
public class ResponseHelper implements Serializable {
    private String response; // codice di ritorno
    // Lista di supporto per la risposta, in caso di login contiene gli indirizzi delle chat dei progetti per fare le join
    private List<Chat> address;
    private List<String> list;
    private Map<String,Boolean> userListStatus; // lista utenti e il loro stato

    public ResponseHelper(){}


    public void setResponse(String response){
        this.response=response;
    }
    public void setChatAddress(List<Chat> address){
        this.address=address;
    }
    public void setUserListStatus(Map<String,Boolean> userListStatus){
        this.userListStatus=userListStatus;
    }
    public void setList(List list) { this.list = list;}
    public String getResponse(){
        return response;
    }
    public List<Chat>  getChatAddress(){
        return address;
    }
    public Map<String,Boolean> getUserListStatus(){
        return userListStatus;
    }
    public List<String> getList(){ return list; }
}
