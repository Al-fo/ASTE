package aste;

import java.io.IOException;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class Lotto implements Serializable{
    public final int ID_LOTTO;
    private static int nextId = 1;
    private double valoreAttuale;
    private String nomeLotto;
    private List<Oggetto> oggettiCompresi;
    public static final int PORTA = 3200;
    private InetAddress indirizzoMulticast;
    private String vincitoreAttuale;
    private double prezzoBase;
    private double rilancioMinimo;

    public Lotto(String nomeLotto, double prezzoBase, double rilancioMinimo) throws UnknownHostException{
        ID_LOTTO = nextId;
        nextId++;
        indirizzoMulticast = InetAddress.getByName("127.0.0." + ID_LOTTO);
        this.nomeLotto = nomeLotto;
        this.prezzoBase = prezzoBase;
        this.rilancioMinimo = rilancioMinimo;
        oggettiCompresi = new ArrayList<>();
        valoreAttuale = getPrezzoMinimo();
    }

    public double getPrezzoMinimo(){
        return prezzoBase / 100 * 80;
    }

    public String getNome(){
        return nomeLotto;
    }

    public int getID() {
        return ID_LOTTO;
    }

    public int numOggetti(){
        return oggettiCompresi.size();
    }
    
    public String getVincitoreAttuale() {
        return vincitoreAttuale;
    }

    public InetAddress getIndirizzoMulticast() {
        return indirizzoMulticast;
    }

    public void inserisciProdotti(Oggetto[] prodotti) throws IOException{
        for(Oggetto o: prodotti){
            if(o == null) throw new IOException("Prodotti non validi");
            for(Oggetto oo: oggettiCompresi){
                if(o.getID() == oo.getID()) throw new IOException("Prodotti già presenti");
            }
        }
        for(Oggetto o: prodotti){
            oggettiCompresi.add(o);
        }
    }

    public synchronized void effettuaRilancio(double nuovoValore, String nomeRilanciatore) throws IOException{
        if(nuovoValore < valoreAttuale + rilancioMinimo) throw new IOException("Il rilancio minimo è di " + rilancioMinimo);
        valoreAttuale = nuovoValore;
        vincitoreAttuale = nomeRilanciatore;
    }
}
