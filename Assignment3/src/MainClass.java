import java.util.*;
public class MainClass {
    public void main(String[] args){
        Scanner scanner=new Scanner(System.in);
        System.out.println("Inserire il numero di codici rossi");
        int red=scanner.nextInt();
        System.out.println("Inserire il numero di codici gialli");
        int yellow=scanner.nextInt();
        System.out.println("Inserire il numero di codici bianchi");
        int white= scanner.nextInt();

        for(int i=0; i<Ambulatorio.medici.length;i++){
            Ambulatorio.medici[i]=false;
        }




    }
}
