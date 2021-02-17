import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.sound.midi.SysexMessage;
import java.io.*;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.RemoteObject;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

public class ServerMain extends RemoteObject implements ServerRMI {
    private static final long serialVersionUID = 1L;
    private List<Project> projects;
    private List<User> users;
    private Map<Notify,String> clientStubs; // stub che il client mi passa con il metodo RMI register
    private Map<SocketChannel,String> socketNickname; // associa socket e nickname utente
    private ObjectMapper mapper;
    private File worthDirectory,userJSON,projectJSON;


    public ServerMain(){
        super();
        mapper=new ObjectMapper();
        mapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
        clientStubs= new HashMap<>();
        socketNickname=new HashMap<>();
        worthDirectory=new File("./Worth");
        retrieveFiles();
    }
    public void retrieveFiles(){
        userJSON=new File(worthDirectory + "/Users.json");
        projectJSON=new File(worthDirectory + "/Projects.json");
        try {
            if (!worthDirectory.exists()) worthDirectory.mkdir();
            if (!userJSON.exists()){
                userJSON.createNewFile();
                users=new ArrayList<>();
                mapper.writeValue(userJSON,users);
            }
            else users=new ArrayList<>(Arrays.asList(mapper.readValue(userJSON,User[].class))); // oppure con Typereference
            if (!projectJSON.exists()){
                projectJSON.createNewFile();
                projects=new ArrayList<>();
                mapper.writeValue(projectJSON,projects);
            }
            else projects=new ArrayList<>(Arrays.asList(mapper.readValue(projectJSON,Project[].class)));
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
            serverChannel.socket().bind(new InetSocketAddress(6789));
            serverChannel.configureBlocking(false);

            // creo il selettore e registro il serversocketchannel
            selector=Selector.open();
            serverChannel.register(selector, SelectionKey.OP_ACCEPT);
        }
        catch(IOException e){
            e.printStackTrace();
            return;
        }
        System.out.println("Server waiting for connections on port 6789");
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
                        client.register(selector,SelectionKey.OP_READ);
                    }
                    else if (key.isReadable()){
                        SocketChannel client= (SocketChannel) key.channel();
                        ByteBuffer buffer= ByteBuffer.allocate(256);
                        client.read(buffer);
                        String request=new String(buffer.array()).trim();
                        String [] command= request.split(" "); //  splittare comando e argomenti
                        String response="";
                        switch(command[0].toLowerCase()){
                            case "login":
                                ResponseHelper<Chat> log = new ResponseHelper();
                                socketNickname.put(client,command[1]);
                                if(command.length!=3)log.setResponse("Wrong arguments");
                                else{
                                    log=login(command[1],command[2]); // command[1] e command[2] sono nick e password
                                }

                                key.attach(log); // attachment al channel, conterrà la risposta da mandare al client
                                break;
                            case "logout":
                                ResponseHelper<Chat> logO=new ResponseHelper();
                                if(command.length!=2) response="Wrong arguments";
                                else if(!socketNickname.get(client).equalsIgnoreCase(command[1])) response="Wrong nickname";
                                else response=logout(command[1]);
                                logO.setResponse(response);
                                key.attach(logO);
                                break;
                            case "createproject":
                                ResponseHelper<String> createP=new ResponseHelper();
                                if(command.length!=2) response="Wrong arguments";
                                else response=createProject(command[1],socketNickname.get(client));
                                createP.setResponse(response);
                                key.attach(createP);
                                break;
                            case "addmember":
                                ResponseHelper<String> addM = new ResponseHelper();
                                if(command.length!=3) response="Wrong arguments";
                                else response=addMember(command[1],socketNickname.get(client),command[2]);
                                addM.setResponse(response);
                                key.attach(addM);
                                break;
                            case "showmembers":
                                ResponseHelper<String> membersList=new ResponseHelper();
                                if(command.length!=2) membersList.setResponse("Wrong arguments");
                                else membersList=showMembers(command[1],socketNickname.get(client));
                                key.attach(membersList);
                                break;
                            case "showcards":
                                ResponseHelper<String> cardsList = new ResponseHelper();
                                if(command.length!=2) cardsList.setResponse("Wrong arguments");
                                else cardsList=showCards(command[1],socketNickname.get(client));
                                key.attach(cardsList);
                                break;
                            case "showcard":
                                ResponseHelper<String> cardList = new ResponseHelper();
                                if(command.length!=3) cardList.setResponse("Wrong arguments");
                                else cardList=showCard(command[1],command[2],socketNickname.get(client));
                                key.attach(cardList);
                                break;
                            case "addcard":
                                ResponseHelper<String> addC=new ResponseHelper();
                                if(command.length<4) response="Wrong arguments";
                                else response=addCard(request,socketNickname.get(client));
                                addC.setResponse(response);
                                key.attach(addC);
                                break;
                            case "movecard":
                                ResponseHelper<String> moveC= new ResponseHelper();
                                if(command.length!=5) response="Wrong arguments";
                                else response=moveCard(command[1],command[2],command[3],command[4],socketNickname.get(client));
                                moveC.setResponse(response);
                                key.attach(moveC);
                                break;
                            case "getcardhistory":
                                ResponseHelper<String> cardHistoryList= new ResponseHelper();
                                if(command.length!=3) cardHistoryList.setResponse("Wrong arguments");
                                else cardHistoryList=getCardHistory(command[1],command[2],socketNickname.get(client));
                                key.attach(cardHistoryList);
                                break;
                            case "cancelproject":
                                ResponseHelper<String> cancelP = new ResponseHelper();
                                if(command.length!=2) response="Wrong arguments";
                                else response=cancelProject(command[1],socketNickname.get(client));
                                cancelP.setResponse(response);
                                key.attach(cancelP);
                                break;
                            case "quit":
                                System.out.println("User " + socketNickname.get(client) + " closing connection");
                                for(User u : users){
                                    if(u.getNick().equals(socketNickname.get(client))){
                                        if(u.getStatus()) logout(socketNickname.get(client));
                                    }
                                }
                                socketNickname.remove(client);
                                client.close();
                                key.cancel();
                                break;
                        }
                        if(key.isValid()) key.interestOps(SelectionKey.OP_WRITE);
                    }
                    else if(key.isWritable()){
                        SocketChannel client= (SocketChannel)key.channel();
                        ResponseHelper<?> response = (ResponseHelper<?>) key.attachment();

                        client.write(ByteBuffer.wrap(mapper.writeValueAsBytes(response)));
                        key.interestOps(SelectionKey.OP_READ);
                    }
                }
                catch(IOException e){
                    key.cancel();
                    socketNickname.remove(key.channel());
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
        try { update(nick,u.getStatus()); } // callbacks
        catch(RemoteException e){
            e.printStackTrace();
        }
        users.add(u);
        try {
            mapper.writeValue(userJSON, users);
        }
        catch(IOException e){e.printStackTrace();}
        System.out.println("User " + nick + " registered");
        return "OK";
    }
    public ResponseHelper<Chat> login(String nick, String pass){
        ResponseHelper<Chat> response=new ResponseHelper<>();
        List<Chat> chatAddress=new ArrayList<>();
        if (nick.isEmpty() || pass.isEmpty()){
            response.setResponse("Empty field");
            return response;
        }
        for(User u : users){
            if (u.getNick().equalsIgnoreCase(nick)){
                if(u.getStatus()){
                    response.setResponse("User already logged in");
                    return response;
                }
                if(!u.getPass().equals(pass)){
                    response.setResponse("Wrong password");
                    return response;
                }
                else{
                    u.setStatus(true);
                    try { update(nick,u.getStatus()); } // callbacks
                    catch(RemoteException e){
                        e.printStackTrace();
                    }
                    System.out.println("User " + nick + " logged in");
                    for(Project p : projects){
                        if(p.isMember(nick)){
                            Chat c=new Chat(p.getProjectID(),p.getChatAddress(),p.getPort()); // restituisco all'utente la lista degli indirizzi delle chat
                            chatAddress.add(c);
                        }
                    }
                    response.setResponse("OK");
                    if(!chatAddress.isEmpty()) response.setList(chatAddress);
                    return response;
                }
            }
        }
        System.out.println("User " + nick + " login failed");
        response.setResponse("User not found");
        return response;
    }
    public String logout(String nick){
        if(nick.isEmpty()) return "Empty field";
        for(User u : users){
            if(u.getNick().equalsIgnoreCase(nick)){
                if(!u.getStatus()) return "User is already offline";
                u.setStatus(false);
                try { update(nick,u.getStatus()); } // callbacks
                catch(RemoteException e){
                    e.printStackTrace();
                }
                System.out.println("User " + nick + " logged out");
                return "OK";
            }
        }
        System.out.println("User " + nick + " logout failed");
        return "User not found";

    }
    public String createProject(String projectName,String nick){
        if(projectName.isEmpty()||nick.isEmpty()) return "Empty field";
        for(Project p : projects){
            if(p.getProjectID().equalsIgnoreCase(projectName)) return "Project name already exist";
        }
        String multicastAddress = Utils.generateMulticast();
        int port= Utils.randBetween(1025,65536);
        Project project = new Project(nick,projectName,multicastAddress,port);
        projects.add(project);
        try{
            mapper.writeValue(projectJSON,projects); // aggiorno il file
        }
        catch(IOException e){
            e.printStackTrace();
        }
        Chat chat=new Chat(projectName,multicastAddress,port);

        try { updateChat(chat,nick); } // mando al creatore del progetto l'indirizzo della chat
        catch(RemoteException e ){
            e.printStackTrace();
        }
        System.out.println("Project " + projectName + " created");
        return "Project created";
    }
    public String addMember(String projectName, String nick, String newUser){
        if(projectName.isEmpty()||nick.isEmpty()||newUser.isEmpty()) return "Empty field";
        for(User u : users){
            if (u.getNick().equalsIgnoreCase(newUser)){
                for(Project p : projects){
                    if(p.getProjectID().equalsIgnoreCase(projectName)){
                        if (!p.isMember(nick)){
                            return "User isn't a member";
                        }
                        Chat chat = new Chat(p.getProjectID(),p.getChatAddress(),p.getPort());

                        try { updateChat(chat,newUser); } // mando indirizzo della chat all'utente
                        catch(RemoteException e ){
                            e.printStackTrace();
                        }
                        return p.addMember(newUser);
                    }
                }
                return "Project not found";
            }
        }
        return "User not found";
    }
    public ResponseHelper<String> showMembers(String projectName,String nick){
        ResponseHelper<String> response=new ResponseHelper<>();
        if(projectName.isEmpty()||nick.isEmpty()){
            response.setResponse("Empty field");
            return response;
        }
        for(Project p : projects){
            if(p.getProjectID().equalsIgnoreCase(projectName)){
                if (!p.isMember(nick)){
                    response.setResponse("User isn't a member");
                    return response;
                }
                response.setList(p.getMembers());
                response.setResponse("OK");
                return response;
            }
        }
        response.setResponse("Project not found");
        return response;
    }
    public ResponseHelper<String> showCards(String projectName,String nick){
        ResponseHelper<String> response=new ResponseHelper<>();
        if(projectName.isEmpty()||nick.isEmpty()){
            response.setResponse("Empty field");
            return response;
        }
        for(Project p : projects){
            if(p.getProjectID().equalsIgnoreCase(projectName)){
                if (!p.isMember(nick)){
                    response.setResponse("User isn't a member");
                    return response;
                }
                response.setList(p.getCardsList());
                response.setResponse("OK");
                return response;
            }
        }
        response.setResponse("Project not found");
        return response;
    }
    public ResponseHelper<String> showCard(String projectName,String cardName,String nick){
        ResponseHelper<String> response=new ResponseHelper<>();
        if(projectName.isEmpty()||nick.isEmpty()||cardName.isEmpty()){
            response.setResponse("Empty field");
            return response;
        }
        for(Project p : projects){
            if(p.getProjectID().equalsIgnoreCase(projectName)){
                if (!p.isMember(nick)){
                    response.setResponse("User isn't a member");
                    return response;
                }
                List<String> tmp;
                if((tmp=p.retrieveCard(cardName))==null) response.setResponse("Card not found");
                else response.setResponse("OK");
                response.setList(tmp);
                return response;
            }
        }
        response.setResponse("Project not found");
        return response;
    }
    public String addCard(String command, String nick){
        String [] commands = command.split(" ");
        String projectName=commands[1];
        String cardName=commands[2];
        String description="";
        for(int i=3;i<commands.length;i++){
            description+=commands[i] + " ";
        }
        if(commands[1].isEmpty()||commands[2].isEmpty()||commands[3].isEmpty()||nick.isEmpty()){
            return "Empty field";
        }
        for(Project p : projects){
            if(p.getProjectID().equalsIgnoreCase(commands[1])){
                if(!p.isMember(nick)){
                    return "User isn't a member";
                }
                try{return p.addCard(commands[2],description);}
                catch(IOException e){
                    e.printStackTrace();
                }
            }
        }
        return "Project not found";
    }
    public String moveCard(String projectName,String cardName,String start,String end,String nick) {
        if (projectName.isEmpty() || start.isEmpty() || end.isEmpty() || cardName.isEmpty() || nick.isEmpty()) return "Empty field";
            for (Project p : projects) {
                if (p.getProjectID().equalsIgnoreCase(projectName)) {
                    if (!p.isMember(nick)) {
                        return "User isn't a member";
                    }
                    try {
                        return p.moveCard(cardName, start, end);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return "Project not found";
    }
    public ResponseHelper<String> getCardHistory(String projectName,String cardName,String nick){
        ResponseHelper<String> response=new ResponseHelper<>();
        if(projectName.isEmpty()||cardName.isEmpty()||nick.isEmpty()){
            response.setResponse("Empty field");
            return response;
        }
        for (Project p : projects) {
            if (p.getProjectID().equalsIgnoreCase(projectName)) {
                if (!p.isMember(nick)) {
                    response.setResponse("User isn't a member");
                }
                List<String> tmp;
                if((tmp=p.retrieveCardHistory(cardName))==null) response.setResponse("Card not found");
                else response.setResponse("OK");
                response.setList(tmp);
                return response;
            }
        }
        response.setResponse("Project not found");
        return response;
    }
    public String cancelProject(String projectName, String nick){
        if(projectName.isEmpty()||nick.isEmpty()) return "Empty field";
        for(Project p : projects){
            if(p.getProjectID().equalsIgnoreCase(projectName)){
                if(!p.isMember(nick)) return "User isn't a member";
                if(!p.canDelete()) return "Can't delete project: all the cards have to be in TODO list";
                try{
                    p.deleteProject();
                    mapper.writeValue(projectJSON,projects);
                    updateremoveChat(new Chat(p.getProjectID(),p.getChatAddress(),p.getPort()));
                    projects.remove(p);
                    System.out.println("Project " + projectName + " removed");
                    return "Project canceled";
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return "Project doesn't exist";
    }
    // CALLBACKS
    public synchronized Map<String,Boolean> registerCB(Notify client,String nick) throws RemoteException{
        Map<String,Boolean> userList=new HashMap<>();
        this.clientStubs.put(client,nick); // salvo lo stub del client//
        for (User u : users){
            userList.put(u.getNick(),u.getStatus()); // lista degli utenti online e del loro stato
        }
        System.out.println("Client registered for callbacks");
        return userList;
    }
    public synchronized void unregisterCB(Notify client,String nick) throws RemoteException{
        if(clientStubs.remove(client,nick)) System.out.println("Client unregistered for callbacks");
        else System.out.println("Unable to unregister client for callbacks");
    }
    public void update(String nick,Boolean status)throws RemoteException {
        doCallbacks(nick,status);
    }
    public void updateChat(Chat chat,String nick)throws RemoteException{
        doChatCallbacks(chat,nick);
    }
    public void updateremoveChat(Chat chat) throws  RemoteException{
        doRemoveChatCallbacks(chat);
    }
    public synchronized void doRemoveChatCallbacks(Chat chat) throws RemoteException{
        Project p=null;
        for(Project tmp : projects){
            if(chat.getProjectName().equalsIgnoreCase(tmp.getProjectID())){
                p=tmp;
                break;
            }
        }
        if(p!=null) {
            for (Notify stub : clientStubs.keySet()) {
                if (p.isMember(clientStubs.get(stub))) stub.notifyProjectCancel(chat);
                System.out.println("Callbacks remove project done");
            }
        }
    }
    public synchronized void doChatCallbacks(Chat chat,String nick)throws RemoteException{
        for(Notify stub : clientStubs.keySet()){
            if(clientStubs.get(stub).equalsIgnoreCase(nick)) stub.notifyProject(chat);
        }
        System.out.println("Chat callbacks done");
    }
    public synchronized void doCallbacks(String nick, Boolean status)throws RemoteException{
        for(Notify stub : clientStubs.keySet()){
            stub.notifyUser(nick,status);
        }
        System.out.println("Callbacks done");
    }
    public static void main(String [] args){
        ServerMain server = new ServerMain();
        try{
            ServerRMI stub = (ServerRMI) UnicastRemoteObject.exportObject(server,0);
            LocateRegistry.createRegistry(2021);
            Registry r = LocateRegistry.getRegistry(2021);
            r.rebind("WorthServer",stub);
            server.startServer();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
