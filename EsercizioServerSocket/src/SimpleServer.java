import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class SimpleServer {
    public static void main(String [] args) throws IOException {
        int myport=6789;

        ServerSocket listenSocket= new ServerSocket(myport);
        while(true){
            System.out.println ("Web server waiting for request on port " + myport);

            Socket socket = listenSocket.accept();
            System.out.println ("Connected");

            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            DataOutputStream out= new DataOutputStream(socket.getOutputStream());

            File file= new File("testo.txt");

            int numBytes= (int) file.length();

            try(FileInputStream fileinput = new FileInputStream(file)){
                byte[] fileBytes = new byte[numBytes];
                fileinput.read(fileBytes);
                out.write(fileBytes,0,numBytes);
                out.flush();
                System.out.println(in.readLine());
            }
            catch(FileNotFoundException e){
                out.writeBytes("File not found");
            }
            catch(IOException e){
                e.printStackTrace();
            }
            socket.close();
        }
    }
}
