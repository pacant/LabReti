import java.io.File;
import java.util.*;
import java.util.concurrent.locks.*;

public class Producer implements Runnable{
    private LinkedList<String> files;
    private Lock lock;
    private Lock lockup;
    private Condition notEmpty;
    public Producer(LinkedList<String> files, Lock lock, Condition notEmpty,Lock lockup){
        this.lock=lock;
        this.notEmpty=notEmpty;
        this.files=files;
        this.lockup=lockup;
    }
    public void run()
    {
       gestionefile(Global.filename); //chiamo la funzione ricorsiva sul path iniziale
       lock.lock();
       Global.up=false;
       notEmpty.signalAll(); //sveglio tutti i consumer in attesa che si riempia la coda
       lock.unlock();

    }
    private void gestionefile(String file){
            lock.lock();
            File newfile=new File(file);
            if (newfile.exists()) files.add(file);
            notEmpty.signalAll();
            lock.unlock();
            if(!newfile.exists()) System.out.println(file + " Non esiste");
            //else System.out.println(file + " esiste");
            //System.out.println("Producer says : " + files.peek());
        if(newfile.isDirectory()){
            //System.out.println("è una directory");
            File[] tmps=newfile.listFiles();
            for(File tmp : tmps){
                String tmpname=tmp.getName();
                gestionefile(newfile.getAbsolutePath() + "/" + tmpname);
            }
        }
    }
}
