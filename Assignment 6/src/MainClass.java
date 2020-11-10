import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.lang.reflect.Array;
import java.util.*;
import java.io.*;

public class MainClass {
    public static void main(String [] args) throws IOException {
        Generatore.generate();
        Lettore lettore=new Lettore();
        Thread thread=new Thread(lettore);
        thread.start();
        try{
            thread.join();
        }
        catch(Exception e){
            e.printStackTrace();
        }
        System.out.println("Numero Bollettini: " + Generatore.numBollettini);
        System.out.println("Numero F24: " + Generatore.numF24);
        System.out.println("Numero Accrediti: " + Generatore.numAccrediti);
        System.out.println("Numero Bonifici: " + Generatore.numBonifici);
        System.out.println("Numero Pagobancomat: " + Generatore.numPagobancomat);

    }
}

