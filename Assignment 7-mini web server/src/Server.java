import java.io.*;
import java.net.*;
import java.util.Date;
import java.util.StringTokenizer;

public class Server implements Runnable {
    static final File ROOT= new File(".");
    private Socket socket;

    public Server (Socket socket){
        this.socket=socket;
    }

    private byte[] readFileData(File file, int lenght) throws IOException { // legge la richiesta dalla socket e la mette in un byte[]

        byte[] data = new byte[lenght];
        FileInputStream in = null;

        try {
            in = new FileInputStream(file);
            in.read(data);
        } finally {
            if (in != null)
                in.close();
        }
        return data;
    }
    private String getContentType(String filename) { // per restituire il tipo di contenuto del file
        if(filename.endsWith(".htm") || filename.endsWith(".html"))
            return "text/html";
        else if(filename.endsWith(".gif"))
            return "image/gif";
        else if(filename.endsWith(".jpg"))
            return "image/jpg";
        else return "text/plain";
    }

    public void run(){
        BufferedReader reader= null; //legge la richiesta http
        BufferedOutputStream writer = null; // output del file in byte
        String file= null;  // file richiesto dal browser web
        PrintWriter headerWriter= null; // header output

        try{
            reader=new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer=new BufferedOutputStream(socket.getOutputStream());
            headerWriter=new PrintWriter(socket.getOutputStream());
            String request= reader.readLine();
            String method=null;

            if(request!=null){
                StringTokenizer tokenizer = new StringTokenizer(request);
                method= tokenizer.nextToken().toUpperCase();
                file=tokenizer.nextToken().toLowerCase();
            }

            if(!method.equals("GET")){ // voglio solo il metodo GET
                System.err.println("Only GET method supported");
            }
            else{
                File f= new File(ROOT,file);
                int fileLength = (int) f.length();
                byte[] data = readFileData(f,fileLength);

                // print header
                headerWriter.println("HTTP/1.1 200 OK"); // richiesta accettata
                headerWriter.println("SERVER: Java HTTP Server");
                headerWriter.println("DATE: " + new Date());
                headerWriter.println("CONTENT-TYPE: " + getContentType(file));
                headerWriter.println("CONTENT-LENGHT: " + fileLength);
                headerWriter.println();
                headerWriter.flush();

                writer.write(data,0,fileLength); // mando il file
                writer.flush();

                System.out.println("File " + file + " returned");
            }
        } catch (FileNotFoundException nf){
            System.err.println("404: FILE " + file + " NOT FOUND"); // file richiesto non presente
        }
        catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error");
        }
        finally{
            try {
                // chiudo streams e socket
                reader.close();
                writer.close();
                headerWriter.close();
                socket.close();
            } catch (IOException e) {
                System.err.println("Error closing streams");
            }


        }
    }
}
