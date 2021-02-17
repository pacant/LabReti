import java.io.*;
import java.nio.channels.SocketChannel;
import java.util.List;
import java.util.Map;
import java.util.Random;
// metodi ausiliari
public class Utils {
    public static int randBetween(int low,int high){ // per generare un numero fra low(inclusivo) e high(esclusivo), per comodità
        Random r=new Random();
        return r.nextInt(high-low) + low;
    }
    public static String generateMulticast(){ // genera un indirizzo multicast, quindi compreso fra 224.0.0.0 e 239.255.255.255
        return randBetween(224,240) + "." + randBetween(0,256)+ "." + randBetween(0,256)+ "." + randBetween(0,256);
    }

    public static void printList(List<String> list){
        for(String u : list){
            System.out.println("-" + u);
        }
    }
}
