import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class Ambulatorio {
    static public boolean [] medici=new boolean[10]; //un array contenente flag che indicano se un medico è occupato o meno.
    static public boolean full=false;  // indica se ci sono codici rossi in attesa
    static public int redwaiting=0;
}
