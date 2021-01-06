import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class MainClass {
    public static int DEFAULT_PORT=6789;
    public static void main(String[] args){
        int port=DEFAULT_PORT;
        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.out.println("Inserire il numero di porta correttamente");
                System.exit(-1);
            }
        }
        try {
            SocketChannel client=SocketChannel.open(new InetSocketAddress(port)); //connessione al server e creazione Socketchannel
            ByteBuffer buffer=ByteBuffer.allocate(8192);
            Scanner scanner= new Scanner(System.in);
            String data;
            boolean isUp=true;
            System.out.println("Hi, say something. (type \"exit\" to terminate)");
            while (isUp) {
                // scrittura

                data= scanner.nextLine(); //leggo da input
                buffer.put(data.getBytes()); //inserisco l'input nel ByteBuffer
                buffer.flip(); //modalità lettura
                while (buffer.hasRemaining()) client.write(buffer); // write nel channel
                if(data.equalsIgnoreCase("exit")){
                    isUp=false;
                    continue;
                }
                buffer.clear(); // modalità scrittura

                //lettura
                ByteBuffer buf= ByteBuffer.allocate(data.length()+17);
                int letti=client.read(buf);
                //System.out.println("LETTI " + letti);
                buf.flip(); // modalità lettura
                System.out.println(new String(buf.array()).trim() + " echoed by server");

            }
            System.out.println("Bye");
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }
}
