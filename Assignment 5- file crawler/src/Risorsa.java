import java.io.File;
import java.util.*;

public class Risorsa {
    private LinkedList<String> files;
    boolean stop=false;

    public Risorsa(LinkedList<String> files){
        this.files=files;
    }

    public synchronized void take() {
        while (files.isEmpty()) {
            try {
                this.wait();
            } catch (InterruptedException e){
                stop=true;
            }
        }
        if(stop) return;
        if(!files.isEmpty()){
            File temp = new File(files.poll());
            System.out.println(temp.getName());
        }
    }

    public synchronized void put(String file) {
        File newfile = new File(file);
        if (newfile.exists()) files.add(file);
        this.notifyAll();
        if (newfile.isDirectory()) {
            File[] tmps = newfile.listFiles();
            for (File tmp : tmps) {
                String tmpname = tmp.getName();
                put(newfile.getAbsolutePath() + "/" + tmpname);
            }

        }
    }
}
