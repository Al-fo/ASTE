package aste;

import java.io.IOException;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import javax.naming.NoPermissionException;

public class Lotto implements Serializable{
    public final int ID_LOTTO;
    private String vincitoreAttuale;
    private double valoreAttuale;
    private static int nextId = 1;
    private String nomeLotto;
    private List<Oggetto> oggettiCompresi;
    private InetAddress indirizzoMulticast;
    private static final int PORTA = 3200;
    private double prezzoBase;
    private double rilancioMinimo;

    public Lotto(String nomeLotto, double prezzoBase, double rilancioMinimo) throws UnknownHostException{
        ID_LOTTO = nextId;
        nextId++;
        indirizzoMulticast = InetAddress.getByName("224.0.0." + ID_LOTTO);
        this.nomeLotto = nomeLotto;
        this.prezzoBase = prezzoBase;
        this.rilancioMinimo = rilancioMinimo;
        valoreAttuale = getPrezzoMinimo();
        vincitoreAttuale = "";
        oggettiCompresi = new ArrayList<>();
    }

    public void inserisciProdotti(Oggetto[] prodotti) throws IOException{
        if(prodotti == null) throw new IOException("Prodotti non validi");
        for(Oggetto o: prodotti){
            if(o == null) throw new IOException("Prodotti non validi" + o);
            for(Oggetto oo: oggettiCompresi){
                if(o.getID() == oo.getID()) throw new IOException("Prodotti già presenti");
            }
        }
        for(Oggetto o: prodotti){
            oggettiCompresi.add(o);
        }
    }

    public int getID() {
        return ID_LOTTO;
    }

    public int numOggetti(){
        return oggettiCompresi.size();
    }

    public String getNomeLotto() {
        return nomeLotto;
    }

    public synchronized void effettuaRilancio(double nuovoValore, String nominativo) throws IOException{
        if(nuovoValore < valoreAttuale + rilancioMinimo) throw new IOException("Il rilancio minimo deve essere di " + rilancioMinimo);
        valoreAttuale = nuovoValore;
        vincitoreAttuale = nominativo;
    }

    public String getVincitore(){
        return vincitoreAttuale;
    }

    public double getPrezzoMinimo() {
        return prezzoBase * 4 / 5;
    }

    public InetAddress getIndirizzoMulticast() {
        return indirizzoMulticast;
    }

    public double getValoreAttuale() {
        return valoreAttuale;
    }

    @Override
    public String toString() {
        String s = "";
        s += "Nome: " + getNomeLotto() + "|Valore Attuale: " + getValoreAttuale() + "|VincitoreAttuale: " + (getVincitore() == ""? "Nessuno": getVincitore()) + "|Indirizzo multicast: " + getIndirizzoMulticast().toString();
        return s;
    }
}
