import java.io.Serializable;
import java.util.List;
import java.util.Map;

// oggetti di risposta ad un comando
public class ResponseHelper<T> implements Serializable {
    private String response; // codice di ritorno
    // Lista di supporto per la risposta, in caso di login contiene gli indirizzi delle chat dei progetti per fare le join
    private List<T> list;

    public ResponseHelper(){}


    public void setResponse(String response){
        this.response=response;
    }
    public void setList(List<T> list) { this.list = list;}
    public String getResponse() { return response; }
    public List<T> getList(){ return list; }
}
