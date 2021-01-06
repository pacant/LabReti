import java.io.*;
import java.net.*;
import java.util.*;

//     ----------- PACE ANTONIO 559397 -----------
public class PingServer {
    private static final int MAX_PORT = 665535; // numero di porta max
    private static final int SIZE= 1024; //dimensione byte array

    private static void report(DatagramPacket packet){

        byte[] data = packet.getData(); // dati ricevuti

        ByteArrayInputStream bis = new ByteArrayInputStream(data); // leggo il bytearray come stream di bytes

        InputStreamReader input = new InputStreamReader(bis); // applico il filtro per leggere i bytes come stream di caratteri

        BufferedReader buffreader = new BufferedReader(input); // bufferizzo

        String line="";

        try{
            line= buffreader.readLine();
        }
        catch(IOException e){
            System.out.println("Errore readLine(): " + e.getMessage());
        }
        String clientaddr= packet.getAddress().getHostAddress();
        int clientport= packet.getPort();
        System.out.print(clientaddr + ":" + clientport + "> " + line);
    }
    public static void main(String []args) throws Exception {

        if (args.length !=1 ) {
            System.out.println("INSERIRE NUMERO DI PORTA COME ARGOMENTO");
            System.exit(-1);
        }

        int port=0;
        try{
            port=Integer.parseInt(args[0]);
        }
        catch(NumberFormatException e){
            System.out.println("ERR -arg 1");
            System.exit(-1);
        }
        if (port <0 || port > MAX_PORT) System.out.println("ERR -arg 1");

        DatagramSocket sock = new DatagramSocket(port);
        Random rand = new Random();

        while(true){

            DatagramPacket receivepacket = new DatagramPacket(new byte[SIZE],SIZE); // packet ricezione da client

            sock.receive(receivepacket);

            report(receivepacket);

            if(rand.nextDouble() < 0.25) { // perdita di pacchetti simulata al 25%
                System.out.println(" ACTION : not sent");
                continue;
            }

            double latency =  rand.nextDouble() * 200;
            Thread.sleep((int)latency);

            InetAddress clientaddr = receivepacket.getAddress(); // indirizzo del client
            int clientport = receivepacket.getPort(); // porta del client
            byte[] data= receivepacket.getData(); // dati ricevuti dal client

            DatagramPacket sendpacket= new DatagramPacket(data,data.length,clientaddr,clientport);

            sock.send(sendpacket); // echo al client

            System.out.println(" ACTION:delayed " + (int) latency + " ms");

        }


    }
}
