public class Consumer implements Runnable{
    private Risorsa risorsa;

    public Consumer(Risorsa risorsa){
        this.risorsa=risorsa;
    }


    @Override
    public void run() {
        while(!Thread.interrupted()){
            risorsa.take();
        }
    }
}
