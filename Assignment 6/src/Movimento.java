public class Movimento {
    private String data;
    private Causale causale;

    public Movimento(){ // costruttore vuoto per deserializzazione 

    }
    public Movimento(String data, Causale causale){
        this.data=data;
        this.causale=causale;
    }
    //getters and setters
    public Causale getCausale() {
        return causale;
    }
    public void setCausale(Causale causale){
        this.causale=causale;
    }

    public String getData(){
        return data;
    }
    public void setData(String data){
        this.data=data;
    }
}
