public class MainClass {
    private final static int DEFAULT_PORT= 6789;
    public static void main(String [] args){
        int port=DEFAULT_PORT;
        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.out.println("Inserire il numero di porta correttamente");
                System.exit(-1);
            }
        }
        EchoServer server = new EchoServer(port);
        server.start();
    }
}
