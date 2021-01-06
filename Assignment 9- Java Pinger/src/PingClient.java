import javax.xml.crypto.Data;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;

//     ----------- PACE ANTONIO 559397 -----------

public class PingClient {
    private static final int SIZE=1024;
    private static final int MAX_PORT= 65535;
    private static final int TIMEOUT= 2000; // timeout receive

    private static void report(DatagramPacket packet, int time){
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

        System.out.println(line + " RTT: " + time + " ms");
    }

    private static void stat(int trasmessi, int ricevuti, int persi, int minRTT, float avg, int maxRTT){
        System.out.println();
        System.out.println();
        System.out.println(" ---- PING Statistics ----");
        System.out.println();
        System.out.println(trasmessi + " packets transmitted, " + ricevuti + " packets received, " + persi + "% packet loss");
        System.out.printf("round-trip (ms) min/avg/max = %d / %.2f / %d",minRTT,avg,maxRTT);
    }

    public static void main(String []args) throws Exception {
        if (args.length != 2){
            System.out.println("RICHIESTI DUE ARGOMENTI");
            System.exit(-1);
        }
        InetAddress servername= null;

        try{
            servername= InetAddress.getByName(args[0]);
        }
        catch(UnknownHostException e){
            System.out.println("ERR -arg 1");
            System.exit(-1);
        }

        int port=0;
        try{
            port=Integer.parseInt(args[1]);
        }
        catch(NumberFormatException e){
            System.out.println("ERR -arg 2");
            System.exit(-1);
        }
        if (port <0 || port > MAX_PORT) System.out.println("ERR -arg 2");

        DatagramSocket sock= new DatagramSocket();

        int i=0;
        // variabili per statistiche

        int trasmessi=0;
        int ricevuti=0;
        int RTT=0;
        int persi=0;
        int maxRTT= 0;
        int minRTT = 2000000;
        float sum=0;
        float avg=0;

        // un'iterazione per ogni datagramma UDP al server

        while(i<10){
            // timestamp
            Date data= new Date();
            long time = data.getTime();

            String msg= "PING " + i + " " + time + "\n";

            byte[] buff = new byte[SIZE]; // bytes array per inviare il messaggio
            buff = msg.getBytes(); // converto msg in bytes

            DatagramPacket sendpacket = new DatagramPacket(buff, buff.length,servername,port); // datagram packet da inviare

            sock.send(sendpacket);

            trasmessi++; // incremento i pacchetti trasmessi

            try{
                sock.setSoTimeout(TIMEOUT); // per la receive

                DatagramPacket receivepacket= new DatagramPacket(new byte[SIZE],SIZE);

                sock.receive(receivepacket); // ricevo il packet dal server, 2 sec di timeout ( bloccante )

                ricevuti++;

                data=new Date();
                long time2 = data.getTime();

                RTT= (int) (time2-time);

                report(receivepacket,RTT);
            }
            catch(IOException e){
                System.out.println("PING " + i + " " + time + " RTT: *");
                persi++;
            }
            i++;

            if(RTT < minRTT) minRTT=RTT;
            if(RTT > maxRTT) maxRTT=RTT;

            sum += RTT; //somma RTT per media
        }

        if(persi!=0) persi = (persi*100)/10; // percentuale pacchetti persi
        avg= sum/trasmessi; //RTT MEDIO
        stat(trasmessi,ricevuti,persi,minRTT,avg,maxRTT);

    }
}
