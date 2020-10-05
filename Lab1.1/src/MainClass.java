import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;


public class MainClass {
    public static void main(String [] args){
        double acc;
        long timer;
        Scanner scanner=new Scanner(System.in);
        acc=scanner.nextDouble();
        timer=scanner.nextLong();
        BlockingQueue<Double> queue = new ArrayBlockingQueue<Double>(1);
        Picalculator pc = new Picalculator(acc,queue);
        Thread t=new Thread(pc);
        t.start();
        long nowtime= System.currentTimeMillis();
        try{

            while(System.currentTimeMillis()-nowtime<timer){
                if(!queue.isEmpty()){
                    System.out.println(queue.take());
                    return;
                }
            }
            t.interrupt();
            System.out.println(queue.take());
            return;
        }
        catch(InterruptedException e) {
            System.out.println("interrotto");
            return;
        }
    }
}
