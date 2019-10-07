import java.util.concurrent.BlockingQueue;

public class Picalculator implements Runnable{
    private double acc;
    private BlockingQueue<Double> queue;
    public Picalculator(double acc,BlockingQueue<Double> queue){
        this.acc=acc;
        this.queue=queue;
    }
    public void run(){
            double pi=0;
            int i=1,count=2;
            try{
            while (!Thread.interrupted() && !((Math.abs(Math.PI - pi)) < acc)) {
                if (count % 2 == 0) pi += 4.0 / i;
                else pi -= 4.0 / i;
                count++;
                i += 2;
            }
            queue.put(pi);
        }
        catch(InterruptedException e){
            System.out.println("interrotto");
            return;
        }
    }

}
