import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Lettore implements Runnable {
    private ThreadPoolExecutor executor= (ThreadPoolExecutor)Executors.newCachedThreadPool();

    public void run(){
        /*try {     // SOLUZIONE SENZA NIO
            ObjectMapper objectMapper = new ObjectMapper();
            List<Conto> cc= Arrays.asList(objectMapper.readValue(Paths.get("JSONobj.json").toFile(),Conto[].class));
            for(Conto conto : cc){
                Worker worker=new Worker(conto);
                executor.execute(worker); // invio un conto corrente ad un thread worker nella thread pool
            }
            executor.shutdown();
            executor.awaitTermination(5000, TimeUnit.MILLISECONDS);
        }
        catch(Exception e){
            e.printStackTrace();
        }
        */

        // SOLUZIONE CON NIO
        List<Conto> cc=null; // lista per deserializzazione file json
        ObjectMapper objectMapper=new ObjectMapper();
        ByteBuffer buffer= ByteBuffer.allocate(1024); //buffer per il channel
        String json=""; //stringa per contenere file json in input
        try(FileInputStream fileinput = new FileInputStream(("JSONobj.json"));FileChannel in=fileinput.getChannel()){
            while(in.read(buffer)!=-1){  //leggo a chunk dal file
                buffer.flip(); // modalità lettura
                json=json.concat((new String(buffer.array())).substring(buffer.position(), buffer.limit()));
                buffer.clear(); // modalità scrittura
            }
        }
        catch(IOException e){
            e.printStackTrace();
        }
        try{
            cc=objectMapper.readValue(json,new TypeReference<List<Conto>>(){}); 
        }
        catch(Exception e){
            e.printStackTrace();
        }
        for(Conto conto : cc){
            Worker worker=new Worker(conto);
            executor.execute(worker); // invio un conto corrente ad un thread worker nella thread pool
        }
        executor.shutdown();
        try {
            executor.awaitTermination(5000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
