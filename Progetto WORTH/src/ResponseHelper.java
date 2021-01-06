import java.util.List;
import java.util.Map;

// oggetti di risposta ad un comando
public class ResponseHelper<T> {
    private String response; // codice di ritorno
    // Lista di supporto per la risposta, in caso di login contiene gli indirizzi delle chat dei progetti per fare le join
    private String address;
    private List<T> list;
    private Map<String,Boolean> userListStatus; // lista utenti e il loro stato

    public ResponseHelper(String response, List<Chat> chatAddress, String address){
        this.response=response;
        this.list=list;
        this.userListStatus=userListStatus;
        this.address=address;
    }
    public ResponseHelper(String response, List<Chat> chatAddress){
        this.response=response;
        this.list=list;
    }


    public void setResponse(String response){
        this.response=response;
    }
    public void setChatAddress(List<T> list){
        this.list=list;
    }
    public void setUserListStatus(Map<String,Boolean> userListStatus){
        this.userListStatus=userListStatus;
    }
    public String getResponse(){
        return response;
    }
    public List<T> getChatAddress(){
        return list;
    }
    public Map<String,Boolean> getUserListStatus(){
        return userListStatus;
    }
}
