import java.util.*;
import java.io.*;
import java.util.concurrent.locks.*;

public class MainClass {
    static public void main(String []args) throws InterruptedException {
        Scanner scanner= new Scanner(System.in);
        Global.filename=scanner.nextLine();
        //List<String> filelist =Collections.synchronizedList(new LinkedList<String>());
        LinkedList<String> filelist=new LinkedList<String>();
        Lock lock=new ReentrantLock();
        Condition notEmpty= lock.newCondition();


        Producer producer=new Producer(filelist,lock,notEmpty);
        Thread threadproducer=new Thread(producer);


        int k=scanner.nextInt();
        scanner.close();
        Thread [] consumatori=new Thread[k];
        threadproducer.start();
        Thread threadconsumer;
        for(int i = 0; i < k; i++){
            Consumer consumer=new Consumer(filelist,lock,notEmpty);
            threadconsumer=new Thread(consumer);
            consumatori[i]=threadconsumer;
            threadconsumer.start();
        }
        threadproducer.join();
        System.out.println("Finito");
        for(int i=0;i<k;i++){
            System.out.println("Interrompo " + consumatori[i].getName());
            consumatori[i].interrupt();
        }



    }
}
