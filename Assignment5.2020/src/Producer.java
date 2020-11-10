public class Producer implements Runnable {
    private Risorsa risorsa;
    private String filename;

    public Producer(Risorsa risorsa,String filename){
        this.risorsa=risorsa;
        this.filename=filename;
    }
    @Override
    public void run() {
        risorsa.put(filename);
    }
}
