import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;
import java.net.MulticastSocket;

@JsonIgnoreProperties({"socket"})
public class Chat implements Serializable {
    //informazioni relative ad una chat
    private String projectName;
    private String address;
    private int port;
    private MulticastSocket socket;

    public Chat(){}

    public Chat(String projectName, String address, int port){
        this.projectName=projectName;
        this.address=address;
        this.port=port;
    }
    public Chat(String address, int port){
        this.address=address;
        this.port=port;
    }


    public void setSocket(MulticastSocket socket){
        this.socket=socket;
    }
    public void setProjectName(String projectName){ this.projectName=projectName; }
    public void setAddress(String address){
        this.address=address;
    }
    public void setPort(int port){
        this.port=port;
    }
    public String getAddress(){
        return this.address;
    }
    public int getPort(){
        return this.port;
    }
    public MulticastSocket getSocket(){
        return this.socket;
    }
    public String getProjectName(){ return this.projectName; }

}
