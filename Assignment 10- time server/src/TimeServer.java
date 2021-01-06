import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Date;

// ________PACE ANTONIO 559397________
public class TimeServer {
    private static final int PORT = 6789;
    private static final int WAIT = 2000;
    private static final int SIZE = 256;
    public static void main (String []args){
        if (args.length!=1){
            System.out.println("Insert multicast address");
            System.exit(-1);
        }
        MulticastSocket multicastSocket= null;
        try{
            int i=1;
            multicastSocket = new MulticastSocket(PORT);
            while(i<=10){
                // buffer da inviare
                byte[] buffer=new byte[SIZE];
                String data= new Date().toString();
                buffer=data.getBytes();

                InetAddress address = InetAddress.getByName(args[0]); // indirizzo di multicast
                DatagramPacket packet = new DatagramPacket(buffer,buffer.length,address,PORT); // creo il pacchetto

                multicastSocket.send(packet); // mando il datagramma sulla socket

                System.out.println("Message n° " + i + " - " + data);
                i++;

                try{
                    Thread.sleep(WAIT); // attendo 2 secondi fra le iterazioni

                }
                catch(InterruptedException e){
                    e.printStackTrace();
                }
            }
            multicastSocket.close();
        }
        catch(IOException e){
            System.out.println("Errore creazione multicast");
            multicastSocket.close();
        }
    }
}
