import java.io.IOException;
import java.net.ServerSocket;

public class MainClass {
    final static int port=6789;
    public static void main(String [] args){

        try( ServerSocket serverSocket = new ServerSocket(port);){
            System.out.println("Server listening on port " + port);

            while(true){
                Server server= new Server(serverSocket.accept());  // un thread per ogni collegamento da parte di un client
                Thread threadServer= new Thread(server);
                threadServer.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
