import java.util.*;
import java.util.concurrent.locks.*;

public class MainClass {
    public static void main(String[] args){
        Scanner scanner = new Scanner(System.in);
        System.out.println("Inserire il numero di professori");
        int num_professori = scanner.nextInt();
        System.out.println("Inserire il numero di tirocinanti");
        int num_tirocinanti = scanner.nextInt();
        System.out.println("Inserire il numero di studenti");
        int num_studenti = scanner.nextInt();
        int num_utilizzatori=num_professori+num_tirocinanti+num_studenti;
        int j=0;

        final Lock lock=new ReentrantLock();
        final Condition occupato=lock.newCondition();
        final Lock lockprof=new ReentrantLock();
        final Condition waitprof=lockprof.newCondition();
    }
}
