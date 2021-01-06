import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

// ________PACE ANTONIO 559397________
public class TimeClient {
    private static final int PORT = 6789;
    private static final int SIZE = 256;

    public static void main(String [] args){
        if (args.length!=1){
            System.out.println("ERROR: Insert multicast address");
            System.exit(-1);
        }
        try{
            // mi connetto al gruppo multicast
            MulticastSocket multicastSocket = new MulticastSocket(PORT);
            InetAddress address = InetAddress.getByName(args[0]);
            multicastSocket.joinGroup(address);

            for(int i=1;i<11;i++){
                byte[] buffer=new byte[SIZE]; // buffer per ricezione
                DatagramPacket packet = new DatagramPacket(buffer,buffer.length); // datagram packet per ricezione

                multicastSocket.receive(packet); // ricevo il packet dal server

                String print = new String(new String(packet.getData()).trim());
                System.out.println("TimeServer says : " + print);
            }

            // abbandono il gruppo multicast e chiudo la socket
            multicastSocket.leaveGroup(address);
            multicastSocket.close();
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());

        }
    }
}
