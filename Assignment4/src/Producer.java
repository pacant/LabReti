import java.io.File;
import java.util.*;
import java.util.concurrent.locks.*;

public class Producer implements Runnable{
    private LinkedList<String> files;
    private Lock lock;
    private Condition notEmpty;
    public Producer(LinkedList<String> files, Lock lock, Condition notEmpty){
        this.lock=lock;
        this.notEmpty=notEmpty;
        this.files=files;
    }
    public void run()
    {
        File root=new File(Global.filename);
       gestionefile(root);
    }
    private void gestionefile(File file){
            lock.lock();
            files.add(file.getName());
            notEmpty.signalAll();
            lock.unlock();
            System.out.println("Producer says : " + files.peek());
        if(file.isDirectory()){
            System.out.println("è una directory");
            String[] s=file.list();
            for(String tmpname : s){
                File tmp=new File(tmpname);
                gestionefile(tmp);
            }
        }

    }
}
