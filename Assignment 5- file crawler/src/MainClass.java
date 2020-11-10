import java.util.LinkedList;

public class MainClass {
    public static void main(String[] args) throws InterruptedException {
        if(args.length!=2) throw new IllegalArgumentException("Passare filename e numero di consumer");
        String filename=args[0];
        LinkedList<String> filelist=new LinkedList<String>(); // Linkedlist che contiene i file

        Risorsa r=new Risorsa(filelist);
        Thread producer=new Thread(new Producer(r,filename));
        producer.start();
        int k= Integer.parseInt(args[1]);
        Thread [] consumers=new Thread[k];
        for(int i=0;i<k;i++){
            consumers[i]=new Thread(new Consumer(r));
            consumers[i].start();
        }

        producer.join();

        for(int i=0;i<k;i++){
            consumers[i].interrupt();
        }
        System.out.println("Fine esecuzione");
    }
}
