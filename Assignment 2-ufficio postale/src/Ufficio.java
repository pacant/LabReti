import java.util.*;
import java.util.concurrent.*;
public class Ufficio{
    private ThreadPoolExecutor executor;
    private BlockingQueue<Runnable> queue; //la coda della prima sala
    public Ufficio(LinkedBlockingQueue<Runnable> queue,int k){
        this.queue=queue;
        executor= new ThreadPoolExecutor(4, 4, 200, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<Runnable>(k)); //thread pool con coda limitata a k
        executor.prestartCoreThread(); //apre gli sportelli subito
    }
    public void entrata(int p) throws InterruptedException{
       try {
           for (int i = 1; i <= p; i++) { //faccio entrare le persone nell'ufficio
               User u = new User(i);
               queue.put(u);
           }
       }
       catch(InterruptedException e){
           e.printStackTrace();
           return;
       }
    }
    public void executeTask() throws RejectedExecutionException{
        try {
            //queue.peek() lo prende dalla coda ma non lo elimina. Consigliato al posto di take()
            User utente = (User)queue.peek();
            executor.execute(utente);
            queue.remove();
        }
        catch(RejectedExecutionException e){
            return;
        }
    }
    public void executeAll(){
        while(!queue.isEmpty()) executeTask();
    }
    public void closeOffice(){
        executor.shutdown();
    }
}