package aste;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

public class Lotto implements Serializable{
    public final int ID_LOTTO;
    private static int nextId = 1;
    private String nomeLotto;
    private List<Oggetto> oggettiCompresi;
    private InetAddress indirizzoMulticast;
    private double prezzoBase;
    private double rilancioMinimo;

    public Lotto(String nomeLotto, double prezzoBase, double rilancioMinimo) throws UnknownHostException{
        ID_LOTTO = nextId;
        nextId++;
        indirizzoMulticast = InetAddress.getByName("127.0.0." + ID_LOTTO);
        this.nomeLotto = nomeLotto;
        this.prezzoBase = prezzoBase;
        this.rilancioMinimo = rilancioMinimo;
    }

    public int getID() {
        return ID_LOTTO;
    }

    public int numOggetti(){
        return 
    }
    
}
