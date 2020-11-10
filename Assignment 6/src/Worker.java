public class Worker implements Runnable{
    private Conto conto;
    public Worker(Conto conto){
        this.conto=conto;
    }
    public void run(){
        contaOccorrenze();

    }
    private void contaOccorrenze(){  // conta le occorrenze delle vari causali per ogni movimento in this.conto
        for(Movimento mov : conto.getMovimenti()){
            synchronized (this) {
                switch (mov.getCausale()) {
                    case PAGOBANCOMAT:
                        Generatore.numPagobancomat++;
                        break;
                    case F24:
                        Generatore.numF24++;
                        break;
                    case BONIFICO:
                        Generatore.numBonifici++;
                        break;
                    case ACCREDITO:
                        Generatore.numAccrediti++;
                        break;
                    case BOLLETTINO:
                        Generatore.numBollettini++;
                }
            }
        }
    }
}
