import java.util.*;
import java.util.concurrent.*;


public class Ufficio{
    private ThreadPoolExecutor executor;
    private BlockingQueue<Runnable> queue;
    public Ufficio(LinkedBlockingQueue<Runnable> queue,int k){
        this.queue=queue;
        executor= new ThreadPoolExecutor(4, 4, 200, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<Runnable>(k)); //la coda dovrebbe essere di k
        executor.prestartCoreThread();
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
            while(!queue.isEmpty()) {
                User utente = null;
                utente = (User)queue.peek();
                executor.execute(utente);
                queue.remove();
            }
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