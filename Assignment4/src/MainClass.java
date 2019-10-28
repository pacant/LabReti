import java.util.*;
import java.io.*;
import java.util.concurrent.locks.*;

public class MainClass {
    static public void main(String []args) throws InterruptedException {
        // MainClass filename k
        // filename (directory di partenza
        // k (numero di consumer)
        if(args.length!=2) throw new IllegalArgumentException("Passare filename e numero di consumer");

        Global.filename=args[0];

        LinkedList<String> filelist=new LinkedList<String>(); // Linkedlist che contiene i file

        Lock lock=new ReentrantLock();
        Condition notEmpty= lock.newCondition(); //inizializzo lock e condition
        Lock lockup=new ReentrantLock();


        Producer producer=new Producer(filelist,lock,notEmpty,lockup);
        Thread threadproducer=new Thread(producer);

        int k= Integer.valueOf(args[1]);


        threadproducer.start(); //faccio partire il producer

        Thread threadconsumer;
        for(int i = 0; i < k; i++){ //faccio partire i consumer
            Consumer consumer=new Consumer(filelist,lock,notEmpty,lockup);
            threadconsumer=new Thread(consumer);
            threadconsumer.start();
        }
        threadproducer.join();
        System.out.println("Producer terminato, i consumer finiscono di svuotare la coda.");
    }
}
