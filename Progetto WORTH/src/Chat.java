import java.net.MulticastSocket;

public class Chat {
    //informazioni relative ad una chat
    private MulticastSocket socket;
    private String address;
    private int port;

    public Chat(MulticastSocket socket, String address, int port){
        this.socket=socket;
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

}
