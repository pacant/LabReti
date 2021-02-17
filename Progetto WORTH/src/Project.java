import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Project {
    private String projectID;

    private List<Card> cards;
    private List<String> todoCards;
    private List<String> inProgressCards;
    private List<String> toberevisedCards;
    private List<String> doneCards;
    private List<String> members;

    private String chatAddress;
    private int port;

    private File directory;
    private ObjectMapper mapper;


    // costruttore, nick del creatore del progetto passato come parametro
    public Project(String user, String id, String address, int port) {
        this.projectID = id;
        this.chatAddress = address;
        this.port = port;
        members = new ArrayList<>();
        cards = new ArrayList<>();
        todoCards = new ArrayList<>();
        inProgressCards = new ArrayList<>();
        toberevisedCards = new ArrayList<>();
        doneCards = new ArrayList<>();
        members.add(user);
        this.directory = new File("./Worth/" + projectID);
        if (!this.directory.exists()) this.directory.mkdir(); // CREO LA CARTELLA DEL PROGETTO SU ./PROJECTS
        mapper = new ObjectMapper();
    }

    public Project() { }

    // aggiungere un membro al progetto
    public String addMember(String nick) {
        if (!members.contains(nick)) {
            members.add(nick);
            return "Member added";
        }
        return "The user is already a member of the project";
    }

    // verificare appartenenza di un utente al progetto
    public boolean isMember(String nick) {
        if (members.contains(nick)) return true;
        return false;
    }

    // aggiungere una card al progetto
    public String addCard(String name, String info) throws IOException {
        Card card = new Card(name, info);
        for(Card c : cards){
            if(c.getName().equalsIgnoreCase(name)) return "Card already in the project";
        }cards.add(card);
        todoCards.add(name);
        File filecard = new File(directory + "/" + name + ".json");
        filecard.createNewFile();
        mapper.writeValue(filecard, card);
        return "Card added";
    }

    // recuperare informazioni relative ad una card
    public List<String> retrieveCard(String name) {
        for (Card card : cards) {
            if (card.getName().equalsIgnoreCase(name)) {
                return card.retrieveCard();
            }
        }
        return null;
    }

    // recuperare history di una card
    public List<String> retrieveCardHistory(String name) {
        for (Card card : cards) {
            if (card.getName().equalsIgnoreCase(name)) {
                return card.getHistory();
            }
        }
        return null;
    }
    @JsonIgnore
    public List<String> getCardsList(){ // una lista con solo i nomi delle carte
        List<String> result=new ArrayList<>();
        for(Card c : cards){
            result.add(c.getName());
        }
        return result;
    }
    public String moveCard(String name, String start, String end) throws IOException {
        for(Card card : cards){
            if (card.getName().equalsIgnoreCase(name)){
                switch(card.getStatus().name()){
                    case "TODO" :
                        if(!start.equalsIgnoreCase("TODO")) return "The card isn't in TODO list";
                        if(!end.equalsIgnoreCase("INPROGRESS")) return "The card in TODO can only be moved to INPROGRESS";
                        card.setStatus(cardStatus.valueOf(end.toUpperCase()));
                        todoCards.remove(name);
                        inProgressCards.add(name);
                        break;

                    case "INPROGRESS" :
                        if(!start.equalsIgnoreCase("INPROGRESS")) return "Card isn't in INPROGRESS list";
                        if((!end.equalsIgnoreCase("TOBEREVISED")) && (!end.equalsIgnoreCase("DONE"))) return "The card in INPROGRESS can only be moved to TOBEREVISED or DONE";
                        card.setStatus(cardStatus.valueOf(end.toUpperCase()));
                        inProgressCards.remove(name);
                        if (end.equalsIgnoreCase("TOBEREVISED")) toberevisedCards.add(name);
                        if (end.equalsIgnoreCase("DONE")) doneCards.add(name);
                        break;

                    case "TOBEREVISED" :
                        if(!start.equalsIgnoreCase("TOBEREVISED")) return "The card isn't in TOBEREVISED list";
                        if((!end.equalsIgnoreCase("INPROGRESS")) && (!end.equalsIgnoreCase("DONE"))) return "The card in TOBEREVISED can only be moved to INPROGRESS or DONE";
                        card.setStatus(cardStatus.valueOf(end.toUpperCase()));
                        toberevisedCards.remove(name);
                        if (end.equalsIgnoreCase("INPROGRESS")) inProgressCards.add(name);
                        if (end.equalsIgnoreCase("DONE")) doneCards.add(name);
                        break;

                    case "DONE" :
                        return "The card in DONE can't be moved";
                }
                File filecard = new File(directory + "/" + name + ".json");
                mapper.writeValue(filecard, card);
                return "Card moved";
            }
        }
        return "The card doesn't exist";
    }

    //cancella la directory del progetto con tutti i file al suo interno
    public void deleteProject() throws IOException{
        for (File c : directory.listFiles())
            c.delete();
        if (!directory.delete())
            throw new FileNotFoundException("Failed to delete file: " + directory);
    }
    // controlla se tutte le card sono in DONE per cancellare il progetto
    public boolean canDelete(){
        if(cards.isEmpty()) return true;
        for(Card card : cards){
            if (!card.getStatus().equals(cardStatus.DONE)) return false;
        }
        return true;
    }

    public void setCards(List<Card> cards){
        this.cards=cards;
    }
    public void setProjectID(String projectID){
        this.projectID=projectID;
    }
    public void setTodoCards(List<String> todoCards){
        this.todoCards=todoCards;
    }
    public void setInProgressCards(List<String> inProgressCards){
        this.inProgressCards=inProgressCards;
    }
    public void setToberevisedCards(List<String> toberevisedCards){
        this.toberevisedCards=toberevisedCards;
    }
    public void setDoneCards(List<String> doneCards){
        this.doneCards=doneCards;
    }
    public void setMembers(List<String> members){
        this.members=members;
    }
    public void setChatAddress(String chatAddress){
        this.chatAddress=chatAddress;
    }
    public void setPort(int port){
        this.port=port;
    }
    public void setDirectory(File directory){
        this.directory=directory;
    }
    public List<Card> getCards(){
        return cards;
    }
    public List<String> getTodoCards(){
        return todoCards;
    }
    public List<String> getInProgressCards(){
        return inProgressCards;
    }
    public List<String> getToberevisedCards(){
        return toberevisedCards;
    }
    public List<String> getDoneCards(){
        return doneCards;
    }
    public List<String> getMembers(){
        return members;
    }
    public String getProjectID(){
        return projectID;
    }
    public String getChatAddress(){
        return chatAddress;
    }
    public int getPort(){
        return port;
    }
    public File getDirectory(){
        return directory;
    }
}
