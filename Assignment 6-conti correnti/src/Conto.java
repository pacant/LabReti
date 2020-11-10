import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class Conto {
    private String nome;
    private List<Movimento> movimenti; // lista per tenere traccia dei movimenti

    public Conto(){ // costruttore vuoto per deserializzazione 

    }
    public Conto(String nome, List<Movimento> movimenti){
        this.nome=nome;
        this.movimenti=movimenti;
    }
        // getters and setters
    public String getNome(){
        return nome;
    }
    public void setNome(String nome){
        this.nome=nome;
    }
    public void setMovimenti(List<Movimento> movimenti){
        this.movimenti=movimenti;
    }
    public List<Movimento> getMovimenti(){
        return movimenti;
    }
}
