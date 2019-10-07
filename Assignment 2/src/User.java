import java.util.*;
public class User implements Runnable{
    private int number;
    public User(int i){
        this.number=i;
    }
    public void run(){
        try{
            System.out.println("Utente " + number + " allo sportello.");
            Thread.sleep((long)Math.round(Math.random()*20000));
            System.out.println("Utente " + number + " servito.");
        }
        catch(InterruptedException e){
            return;
        };
    }

}