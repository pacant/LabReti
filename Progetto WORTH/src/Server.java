import com.fasterxml.jackson.core.async.ByteBufferFeeder;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.rmi.server.RemoteObject;
import java.util.*;

public class Server extends RemoteObject implements ServerRMI {
    private List<Project> projects;
    private int portRMI=2021;
    private int serverport=6789;
    private List<User> users;
    private List<Notify> clientStubs; // stub che il client mi passa con il metodo RMI register
    private Map<SocketChannel,String> socketNickname; // associa socket e nickname utente
    //private List<Chat> chatAddress; // indirizzi e socket per chat
    //private Map<SocketChannel, byte []> response; // Struttura ausiliaria per inviare le risposte, ad ogni client corrisponde un array di byte
    private ObjectMapper mapper;
    private File worthDirectory,userJSON,projectJSON;


    public Server(){
        super();
        projects=new ArrayList<>();
        users=new ArrayList<>();
        clientStubs= new ArrayList<>();
        //chatAddress=new ArrayList<>();
        worthDirectory=new File("./Worth");
        userJSON=new File(worthDirectory + "/Users.json");
        projectJSON=new File(worthDirectory + "/Users.json");
        socketNickname=new HashMap<SocketChannel, String>();
        retrieveFiles();
    }
    public void retrieveFiles(){
        try {
            if (!worthDirectory.exists()) worthDirectory.mkdir();
            if (!userJSON.exists()) userJSON.createNewFile();
            else users=mapper.readValue(userJSON,new TypeReference<List<User>>(){}); // oppure User[].class con Arrays.asList
            if (!projectJSON.exists()) projectJSON.createNewFile();
            else projects=mapper.readValue(projectJSON,new TypeReference<List<Project>>(){});
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void startServer(){
        ServerSocketChannel serverChannel;
        Selector selector;
        try{
            // creo il serversocketchannel e lo rendo non bloccante
            serverChannel=ServerSocketChannel.open();
            serverChannel.socket().bind(new InetSocketAddress(serverport));
            serverChannel.configureBlocking(false);

            // creo il selettore e registro il serversocketchannel
            selector=Selector.open();
            serverChannel.register(selector, SelectionKey.OP_ACCEPT);
        }
        catch(IOException e){
            e.printStackTrace();
            return;
        }
        System.out.println("Server waiting for connections on port " + this.serverport);
        while(true){
            try { if (selector.select()==0) continue; }
            catch (IOException e) {
                e.printStackTrace();
                break;
            }
            Set<SelectionKey> sk = selector.selectedKeys(); // set di selected keys
            Iterator<SelectionKey> it= sk.iterator();
            while(it.hasNext()){
                SelectionKey key=it.next();
                it.remove();
                try{
                    if(key.isAcceptable()){ // richiesta di connessione da un client
                        ServerSocketChannel ssc = (ServerSocketChannel) key.channel();
                        SocketChannel client= ssc.accept();
                        System.out.println("Server connected to client " + client.getRemoteAddress());
                        client.configureBlocking(false);
                        SelectionKey keyClient = client.register(selector,SelectionKey.OP_READ);

                    }
                    else if (key.isReadable()){
                        SocketChannel client= (SocketChannel) key.channel();
                        ByteBuffer buffer= ByteBuffer.allocate(256);
                        client.read(buffer);
                        String request=new String(buffer.array()).trim();
                        String [] command= request.split(" "); // serve per splittare comando e argomenti
                        switch(command[0]){
                            case "login":
                                String response="";
                                socketNickname.put(client,command[1]); // RIMUOVERLO NEL LOGOUT
                                if(command.length!=3) response="Wrong arguments";
                                else{
                                    response=login(command[1],command[2]); // command[1] e command[2] sono nick e password
                                }
                                ByteBuffer byteBuffer = ByteBuffer.wrap(response.getBytes());
                                key.attach(byteBuffer); // attachment al channel, conterrà la risposta da mandare al client
                                break;
                        }
                    }
                }
                catch(IOException e){
                    key.cancel();
                    try{
                        key.channel().close();
                    }
                    catch(IOException ex){}
                }
            }
        }
    }
    public String register(String nick, String pass){
        if (pass.isEmpty()) return "Empty password field";
        for(User u: users){
            if (u.getNick().equalsIgnoreCase(nick)) return "User already exist";
        }
        User u= new User(nick,pass);
        // fare callback
        users.add(u);
        try {
            mapper.writeValue(userJSON, users);
        }
        catch(IOException e){e.printStackTrace();}
        return "Registration completed successfully";
    }
    public String login(String nick, String pass){
        if (nick.isEmpty() || pass.isEmpty()) return "Empty fields";
        for(User u : users){
            if (u.getNick().equalsIgnoreCase(nick)){
                if(u.getStatus()) return "User already logged in";
                if(!u.getPass().equals(pass)) return "Wrong password";
                else{
                    u.setStatus(true);
                    // fare callbacks
                    return "Logged in";
                }
            }
        }
        return "User not found";
    }
    // metodo per trasformare un oggetto in un byte[], trovato online.
    public byte [] ObjtoByte(ResponseHelper<?> obj) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(obj);
        oos.flush();
        byte [] data = bos.toByteArray();
        return data;
    }
}
