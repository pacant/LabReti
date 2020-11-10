import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.File;
import java.io.IOException;
import java.util.GregorianCalendar;

public class Generatore {
    // variabili globali per contare il numero delle causali, l'accesso ad esse è garantito in mutua esclusione fra i vari thread
    public static int numBonifici;  
    public static int numF24;
    public static int numBollettini;
    public static int numAccrediti;
    public static int numPagobancomat;
    public static void generate() throws IOException {
        ObjectMapper objmapper = new ObjectMapper();
        ArrayNode cc=objmapper.createArrayNode(); // array contenente gli oggetti json
        ObjectNode conto; // oggetto json che rappresenta un conto
        ObjectNode movimento; // oggetto json che rappresenta un movimento
        for(int j = 0; j < randBetween(3,10); j++) {
            conto= objmapper.createObjectNode();
            conto.put("nome", generaNome());
            ArrayNode mov = conto.putArray("movimenti"); //array di oggetti json movimento
            for (int i = 0; i < randBetween(4,50); i++) {
                movimento = objmapper.createObjectNode();
                movimento.put("causale", generaCausale().toString());
                movimento.put("data", generaData()); 
                mov.add(movimento);
            }
            cc.add(conto);
        }
        File file = new File("JSONobj.json"); // creo il file json
        file.createNewFile();
        objmapper.writeValue(file,cc);

    }
    public static Causale generaCausale(){ // genera casualmente una causale
        Causale res=Causale.PAGOBANCOMAT;
        int n=randBetween(0,4); // genero un numero da 0 a 4
        switch(n){
            case 0: res= Causale.BOLLETTINO;
                break;
            case 1: res=Causale.BONIFICO;
                break;
            case 2: res=Causale.F24;
                break;
            case 3: res=Causale.ACCREDITO;
                break;
            case 4: res=Causale.PAGOBANCOMAT;
                break;
        }
        return res;
    }
    public static String generaNome(){ // genera casualmente un nome tra 11
        String nome="sconosciuto";
        int n=randBetween(0,10);
        switch(n){
            case 0: nome= "Franco";
                break;
            case 1: nome="Roberta";
                break;
            case 2: nome="Francesco";
                break;
            case 3: nome="Carmine";
                break;
            case 4: nome="Antonio";
                break;
            case 5: nome= "Rosa";
                break;
            case 6: nome="Martina";
                break;
            case 7: nome="Lisa";
                break;
            case 8: nome="Leonardo";
                break;
            case 9: nome="Luca";
                break;
            case 10: nome="Alberto";
                break;
        }
        return nome;
    }
    public static String generaData(){ // genera una data casualmente, compresa fra 2018 e 2020

        GregorianCalendar gc = new GregorianCalendar();

        int year = randBetween(2018, 2020);

        gc.set(gc.YEAR, year);

        int dayOfYear = randBetween(1, gc.getActualMaximum(gc.DAY_OF_YEAR));

        gc.set(gc.DAY_OF_YEAR, dayOfYear);

        return (gc.get(gc.YEAR) + "-" + (gc.get(gc.MONTH) + 1) + "-" + gc.get(gc.DAY_OF_MONTH));

    }

    public static int randBetween(int start, int end) { // restituisce un intero random fra start e end
        return start + (int)Math.round(Math.random() * (end - start));
    }

}
