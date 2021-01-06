import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class EchoServer {
    private int port;

    public EchoServer(int port){
        this.port=port;
    }

    public void start() {
        ServerSocketChannel serverChannel;
        Selector selector;
        try {
            // apro Serversocket channel e selector, rendo il channel non bloccante e lo registro nel selector per l'accept
            serverChannel=ServerSocketChannel.open();


            serverChannel.socket().bind(new InetSocketAddress(port));
            serverChannel.configureBlocking(false); // serverchannel non bloccante

            selector=Selector.open();
            serverChannel.register(selector, SelectionKey.OP_ACCEPT); // registro serverchannel per aspettare connessioni sulla select
        }
        catch(IOException e){
            e.printStackTrace();
            return;
        }
        System.out.println("Server waiting for connections on port " + this.port);
        while(true){
            try {
                if (selector.select() == 0) continue;
            }
            catch(IOException e){
                e.printStackTrace();
                break;
            }
            Set<SelectionKey> sk = selector.selectedKeys(); // set di selected keys
            Iterator<SelectionKey> it= sk.iterator();
            while(it.hasNext()){
                SelectionKey key= it.next();
                it.remove(); // rimuovo dal ready set
                try{
                    if(key.isAcceptable()){ // il server è pronto ad accettare nuove connessioni

                        ServerSocketChannel server = (ServerSocketChannel)key.channel();
                        SocketChannel client= server.accept(); // socketchannel verso il client
                        System.out.println("Server connected to client " + client.getRemoteAddress());
                        client.configureBlocking(false); //channel non bloccante
                        SelectionKey key2= client.register(selector,SelectionKey.OP_READ); // aspetto che il socketchannel sia readable
                        ByteBuffer buffer= ByteBuffer.allocate(8192);
                        key2.attach(buffer);
                    }
                    else if(key.isReadable()){ // il client è pronto a mandare
                            SocketChannel client = (SocketChannel) key.channel();
                            System.out.println("Reading from client " + client.getRemoteAddress());
                            ByteBuffer buffer= (ByteBuffer)key.attachment();
                            int letti;
                            String data;
                            // leggo dal client
                            if ((letti= client.read(buffer))<0||(data=new String(buffer.array()).substring(0,letti).trim()).equalsIgnoreCase("exit") ){
                                System.out.println("Client " + client.getRemoteAddress() + " disconnected");
                                key.cancel();
                                key.channel().close(); // chiudo la connessione se ricevo "exit" o se la read non va a buon fine
                            }
                            else{
                                System.out.println("Client " + client.getRemoteAddress() + " says " + data);
                                key.interestOps(SelectionKey.OP_WRITE); // aspetto che il socketchannel sia writable
                            }
                        }
                        else if(key.isWritable()){ // il client è pronto a ricevere
                            SocketChannel client= (SocketChannel) key.channel();
                            System.out.println("Writing to client " + client.getRemoteAddress());
                            ByteBuffer buf = (ByteBuffer) key.attachment();
                            buf.flip(); // lettura
                            int scritti=-1;
                            while(buf.hasRemaining()) scritti= client.write(buf); // scrivo verso il client
                            buf.clear(); // scrittura
                            System.out.println(scritti + " Bytes written");
                            key.interestOps(SelectionKey.OP_READ); // aspetto che il socketchannel sia readable
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
}
