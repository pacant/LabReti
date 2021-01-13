import java.util.ArrayList;
import java.util.List;

public class Card {
    private String name;
    private String info;
    private cardStatus status;
    private List<String> history;

    public Card(){}

    public Card(String name, String info){
        this.name=name;
        this.info=info;
        status=cardStatus.TODO;
        this.history=new ArrayList<>();
        this.history.add("TODO");
    }
    // cambiare la lista in cui si trova la card
    public void changeStatus(cardStatus status){
        this.status=status;
        history.add(status.name());
    }
    // per il comando showCard, per recuperare le informazioni relative ad una carta
    public List<String> retrieveCard(){
        List<String> card = new ArrayList<>();
        card.add(this.name);
        card.add(this.info);
        card.add(this.status.name());
        return card;
    }
    //setters and getters
    public void setName(String name){
        this.name=name;
    }
    public String getName(){
        return this.name;
    }

    public void setInfo(String info){
        this.info=info;
    }

    public String getInfo(){
        return this.info;
    }

    public void setStatus(cardStatus status){
        this.status=status;
    }

    public cardStatus getStatus(){
        return this.status;
    }

    public void setHistory(List<String> history){
        this.history=history;
    }

    public List<String> getHistory(){
        return this.history;
    }
}
