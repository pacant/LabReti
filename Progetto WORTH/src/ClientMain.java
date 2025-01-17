import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.RemoteObject;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

public class ClientMain extends RemoteObject implements Notify {
    private static final long serialVersionUID = 1L;
    private List<Chat> chats; // lista che identifica le chat dei progetti
    private String serverAddress = "localhost";
    private String userName;
    private Map<String, Boolean> userList; // lista degli utenti e il loro stato
    //boolean per lo stato del client. Client up (in attesa di comandi) e utente loggato
    private boolean isUp;
    private boolean loggedIn;
    private ObjectMapper objectMapper;

    public ClientMain() {
        super();
        chats = new ArrayList<>();

        userList = new HashMap<>();
        loggedIn = false;
        isUp = true;
        objectMapper=new ObjectMapper();
    }

    public void startClient() {
        SocketChannel socket=null;
        try {
            Registry r = LocateRegistry.getRegistry(2021);
            ServerRMI stub = (ServerRMI) r.lookup("WorthServer"); // stub del server

            ClientMain client = this;
            Notify clientStub = (Notify) UnicastRemoteObject.exportObject(client, 0); // stub da mandare al server tramite registerCB

            socket = SocketChannel.open();
            socket.connect(new InetSocketAddress(serverAddress, 6789));

            //TODO: STAMPARE I COMANDI POSSIBILI QUI
            printCMDS();
            Scanner scanner = new Scanner(System.in);
            String input = "";
            String[] command;
            String response = "";
            while (isUp) {
                input = scanner.nextLine();
                command = input.split(" ");
                switch (command[0].toLowerCase()) {
                    case "register": // effettuata con RMI
                        if (command.length != 3) System.out.println("Wrong arguments");
                        else if (loggedIn) System.out.println("You are already logged in");
                        else {
                            if ((response = stub.register(command[1], command[2])).equalsIgnoreCase("ok")) {
                                System.out.println("Registration completed successfully");
                            } else System.out.println(response);
                        }
                        break;
                    case "login":
                        if (command.length != 3) System.out.println("Wrong arguments");
                        else if (loggedIn) System.out.println("You are already logged in");
                        else if (login(input, socket)) {
                            userName = command[1];
                            System.out.println("Logged in");
                            loggedIn = true;
                            userList=stub.registerCB(clientStub, userName);
                        }
                        break;
                    case "logout":
                        if (command.length != 2) System.out.println("Wrong arguments");
                        else if (!loggedIn) System.out.println("You aren't logged in");
                        else if (logout(input, socket)) {
                            System.out.println("Logged out");
                            loggedIn = false;
                            stub.unregisterCB(clientStub, userName);
                        }
                        break;
                    case "listusers":
                        if (command.length != 1) System.out.println("Wrong arguments");
                        else if (!loggedIn) System.out.println("You aren't logged in");
                        else printUserList(userList);
                        break;
                    case "listonlineusers":
                        if (command.length != 1) System.out.println("Wrong arguments");
                        else if (!loggedIn) System.out.println("You aren't logged in");
                        else printOnlineUserList(userList);
                        break;
                    case "listprojects":
                        if (command.length != 1) System.out.println("Wrong arguments");
                        else if (!loggedIn) System.out.println("You aren't logged in");
                       else {
                            System.out.println("Project list: ");
                            for (Chat c : chats) {
                                System.out.println(c.getProjectName());
                            }
                        }
                        break;
                    case "createproject":
                    case "cancelproject":
                        if (command.length != 2) System.out.println("Wrong arguments");
                        else if (!loggedIn) System.out.println("You aren't logged in");
                        else receiveString(input, socket);
                        break;
                    case "addmember":
                        if (command.length != 3) System.out.println("Wrong arguments");
                        else if (!loggedIn) System.out.println("You aren't logged in");
                        else receiveString(input, socket);
                        break;
                    case "showmembers":
                        if (command.length != 2) System.out.println("Wrong arguments");
                        else if (!loggedIn) System.out.println("You aren't logged in");
                        else {
                            System.out.println("Members list: ");
                            receiveList(input, socket);
                        }
                        break;
                    case "showcards":
                        if (command.length != 2) System.out.println("Wrong arguments");
                        else if (!loggedIn) System.out.println("You aren't logged in");
                        else {
                            System.out.println("Cards list of project" + command[1] + ": ");
                            receiveList(input, socket);
                        }
                        break;
                    case "help":
                        printCMDS();
                        break;
                    case "showcard":
                        if (command.length != 3) System.out.println("Wrong arguments");
                        else if (!loggedIn) System.out.println("You aren't logged in");
                        else {
                            System.out.println("Information about card " + command[2] + " of project " + command[1] + ": ");
                            receiveList(input, socket);
                        }
                        break;
                    case "addcard":
                        if (command.length < 4) System.out.println("Wrong arguments");
                        else if (!loggedIn) System.out.println("You aren't logged in");
                        else receiveString(input, socket);
                        break;
                    case "movecard":
                        if (command.length != 5) System.out.println("Wrong arguments");
                        else if (!loggedIn) System.out.println("You aren't logged in");
                        else receiveString(input, socket);
                        break;
                    case "getcardhistory":
                        if (command.length != 3) System.out.println("Wrong arguments");
                        else if (!loggedIn) System.out.println("You aren't logged in");
                        else {
                            System.out.println("History about card " + command[2] + ":");
                            receiveList(input, socket);
                        }
                        break;
                    case "readchat":
                        if (command.length != 2) System.out.println("Wrong arguments");
                        else if (!loggedIn) System.out.println("You aren't logged in");
                        else{
                            readChat(command[1]);
                        }
                        break;
                    case "sendchatmsg":
                        if (command.length != 2) System.out.println("Wrong arguments");
                        else if (!loggedIn) System.out.println("You aren't logged in");
                        else{
                            sendChatMsg(command[1]);
                        }
                        break;
                    case "quit": // chiudo la connessione
                        isUp = false;
                        ByteBuffer byteBuffer = ByteBuffer.wrap(input.getBytes());
                        socket.write(byteBuffer);
                        if(loggedIn) stub.unregisterCB(clientStub, userName);
                        loggedIn=false;
                        break;
                    default:
                        System.out.println("Command not found");
                        break;
                }
            }
        } catch (RemoteException | NotBoundException e) {
            if(loggedIn) logout(userName,socket);
            e.printStackTrace();
        } catch (IOException e) {
            if(loggedIn) logout(userName,socket);
            e.printStackTrace();
        }
    }

    public boolean login(String input, SocketChannel socket) {
        ResponseHelper<Chat> response;
        String aux;
        ByteBuffer receive = ByteBuffer.allocate(1024);
        try {
            socket.write(ByteBuffer.wrap(input.getBytes()));

            socket.read(receive);
            aux=new String(receive.array()).trim();
            response=objectMapper.readValue(aux, new TypeReference<ResponseHelper<Chat>>() {});
            if (response.getResponse().equalsIgnoreCase("ok")) {
                if(response.getList()!=null){
                    chats = response.getList(); // ottengo gli indirizzi delle chat dei progetti di cui sono membro. Dopo il login i successivi mi arriveranno via callbacks
                    joinGroups();
                }
                return true;
            }
            System.out.println(response.getResponse());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean logout(String input, SocketChannel socket) {
        ResponseHelper<String> response;
        String aux;

        ByteBuffer receive = ByteBuffer.allocate(1024);
        try {
            socket.write(ByteBuffer.wrap(input.getBytes()));

            socket.read(receive);
            aux = new String(receive.array()).trim();
            response=objectMapper.readValue(aux, new TypeReference<ResponseHelper<String>>() {});
            if (response.getResponse().equalsIgnoreCase("ok")) return true;
            System.out.println(response.getResponse());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }
    public void receiveList(String input, SocketChannel socket) { // metodo unico per ricevere liste di stringhe, per i metodi listproject listonlineuser showmember showcard e showcards
        ByteBuffer receive = ByteBuffer.allocate(1024);
        String aux;
        ResponseHelper<String> response;
        try {
            socket.write(ByteBuffer.wrap(input.getBytes()));
            socket.read(receive);
            aux=new String(receive.array()).trim();
            response=objectMapper.readValue(aux, new TypeReference<ResponseHelper<String>>() {});
            if (response.getResponse().equalsIgnoreCase("ok")) {
                if(response.getList()==null) System.out.println("Lista nulla");
                Utils.printList(response.getList());
            }
        } catch (IOException  e) {
            e.printStackTrace();
        }
    }

    public void receiveString(String input, SocketChannel socket) { // metodo unico per ricevere una stringa, per addmember createproject addcard e movecard
        ByteBuffer receive = ByteBuffer.allocate(1024);
        ResponseHelper<String> response;
        String aux;
        try {
            socket.write(ByteBuffer.wrap(input.getBytes()));
            socket.read(receive);
            aux=new String(receive.array()).trim();
            response=objectMapper.readValue(aux, new TypeReference<ResponseHelper<String>>() {
            });
            System.out.println(response.getResponse());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void joinGroups() { // joinGroup su tutti gli indirizzi multicast dell'utente che ha ricevuto al login
        MulticastSocket multicastSocket;
        try {
            for (Chat c : chats) {
                multicastSocket = new MulticastSocket(c.getPort());
                multicastSocket.joinGroup(InetAddress.getByName(c.getAddress()));
                multicastSocket.setSoTimeout(2500);
                c.setSocket(multicastSocket);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void readChat(String projectname) throws IOException{
        if(projectname.isEmpty()) System.out.println("Empty field");
        DatagramPacket packet;
        for(Chat c : chats){
            if(c.getProjectName().equalsIgnoreCase(projectname)){
                System.out.println("--------");
                while(true){
                    try {
                        byte[] buff=new byte[4096];
                        packet = new DatagramPacket(buff, buff.length);
                        c.getSocket().receive(packet);
                        System.out.println(new String(packet.getData()));
                    } catch (SocketTimeoutException e) {
                        System.out.println("--------");
                        break;
                    }
                }
            }
        }
    }
    // prende solo projectName come parametro, successivamente può mandare in input il messaggio
    public void sendChatMsg(String projectname){
        Scanner scanner= new Scanner(System.in);
        System.out.println("Type a message");
        String message= userName + ": " + scanner.nextLine();
        if(projectname.isEmpty()||message.isEmpty()) System.out.println("Empty field");
        byte[] buff=new byte[8192];
        DatagramPacket packet;
        buff=message.getBytes();
        for(Chat c : chats){
            if(c.getProjectName().equalsIgnoreCase(projectname)){
                try{
                        packet=new DatagramPacket(buff,buff.length,InetAddress.getByName(c.getAddress()),c.getPort());
                        c.getSocket().send(packet);
                        System.out.println("Message sent");
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }
    public void notifyUser(String nick, Boolean status) throws RemoteException {
        boolean isIn = false;
        for (String u : userList.keySet()) {
            if (u.equalsIgnoreCase(nick)) {
                userList.replace(u, status);
                isIn = true;
            }
        }
        if (!isIn) userList.put(nick, status);
        String s = "offline";
        if (status) s = "online";
        System.out.println("User " + nick + " is " + s);
    }

    public void notifyProject(Chat chat) throws RemoteException {
        MulticastSocket multicastSocket;
        try {

            multicastSocket = new MulticastSocket(chat.getPort());
            multicastSocket.joinGroup(InetAddress.getByName(chat.getAddress()));
            multicastSocket.setSoTimeout(2500);
            chat.setSocket(multicastSocket);
            System.out.println("You have been added to project " + chat.getProjectName());
            chats.add(chat);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void notifyProjectCancel(Chat chat) throws RemoteException {
        MulticastSocket multicastSocket = null;
        boolean found = false;
        Chat c;
        Iterator<Chat> it = chats.iterator();
        while (it.hasNext()) {
            c=it.next();
            if (c.getAddress().equalsIgnoreCase(chat.getAddress())) {
                multicastSocket = c.getSocket();
                found = true;
                it.remove();
            }
        }
        if(!found) return;
        try {
            assert multicastSocket != null;
            multicastSocket.leaveGroup(InetAddress.getByName(chat.getAddress()));
            multicastSocket.setSoTimeout(2500);
            System.out.println("The project" + chat.getProjectName() + ",of which you were a member, has been canceled");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void printCMDS(){
        System.out.println("Command list:");
        System.out.println("- register 'username' 'password' ");
        System.out.println("- login 'username' 'password' ");
        System.out.println("- logout 'username'");
        System.out.println("- listUsers");
        System.out.println("- listOnlineUsers");
        System.out.println("- listProjects");
        System.out.println("- createProject 'projectName'");
        System.out.println("- addMember 'projectName' 'userName' ");
        System.out.println("- showMembers 'projectName'");
        System.out.println("- showcards 'projectName'");
        System.out.println("- showcard 'projectName' 'cardName'");
        System.out.println("- addCard 'projectName' 'cardName' 'description'");
        System.out.println("- moveCard 'projectName' 'cardName' 'startList' 'destinationList'");
        System.out.println("- getCardHistory 'projectName' 'cardName'");
        System.out.println("- readChat 'projectName'");
        System.out.println("- sendChatMsg 'projectName");
        System.out.println("- cancelProject 'projectName");
        System.out.println("- help"); // lista dei possibili comandi
        System.out.println("- quit");
        System.out.println("------------------------------");
        System.out.println("Insert command:");
    }
    public static void printUserList(Map<String,Boolean> userList){
        for(String u : userList.keySet()){
            System.out.print("- " + u);
            if(userList.get(u)) System.out.println(" - online");
            else System.out.println(" - offline");
        }
    }
    public static void printOnlineUserList(Map<String,Boolean> userList) {
        for (String u : userList.keySet()) {
            if (userList.get(u)) System.out.println("- " + u);
        }
    }
    public static void main(String[] args){
        ClientMain client= new ClientMain();
        client.startClient();
        System.exit(1);
    }

}
