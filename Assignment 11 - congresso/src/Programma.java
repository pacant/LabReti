import java.io.Serializable;

//__________559397 PACE ANTONIO__________
public class Programma implements Serializable {
    private static long serialVersionUID = 1L; // per la serializzazione
    private int MAX_SESSIONI = 12;  // numero massimo sessioni in una giornata
    private int MAX_SPEAKER = 5;  // numero massimo di speaker in una giornata

    public String[][] day=new String[MAX_SESSIONI][MAX_SPEAKER]; // matrice per memorizzare gli speaker per ogni sessione

        // costruttore
    public Programma(){
        for(int i=0; i< MAX_SESSIONI; i++){
            for(int j=0; j<MAX_SPEAKER;j++){
                day[i][j]="";
            }
        }
    }
    // metodo per la registrazione di uno speaker in una sessione
    public synchronized int registration(int session, String name){
        for(int i=0;i<MAX_SPEAKER;i++){
            if(day[session][i] == ""){
                day[session][i] = name;
                return 0; // success
            }
        }
        return 1; // la sessione è piena
    }

    // stampo una tabella con il programma
    public void printProgram(){
        System.out.println("Sessione\t1° intervento\t\t2° intervento\t\t3° intervento\t\t4° intervento\t\t5° intervento");

        for(int i=0; i<MAX_SESSIONI;i++){
            String num=new String("S" + (i+1));
            for(int j=0;j<MAX_SPEAKER;j++){
                num = num + "\t\t\t" + day[i][j];
            }
        System.out.println(num);
        }
    }

}
