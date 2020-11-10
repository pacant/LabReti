import java.util.*;
public class User implements Runnable{
    private int number;
    public User(int i){
        this.number=i;
    }
    public void run(){
        try{
            System.out.println("Utente " + number + " allo sportello.");
            Thread.sleep((long)Math.round(Math.random()*10000)+1000); // ogni utente fa operazioni da qualche secondo (max circa 11 secondi,min 1 secondo)
            System.out.println("Utente " + number + " servito.");
        }
        catch(InterruptedException e){
            return;
        };
    }

}