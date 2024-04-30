package aste.server;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;

import aste.Lotto;

public class GestoreAste implements Serializable{
    private class Asta implements Serializable{
        private boolean aperta;
        private final int CODICE_ASTA;
        private static int nextCodice = 0;
        private List<Lotto> lottiInAsta;

        public Asta(){
            CODICE_ASTA = nextCodice;
            nextCodice++;
            aperta = true;
        }

        public void aggiungiLotto(Lotto lotto) throws IOException{
            if(!aperta) throw new IOException("Asta finita");
            if(lotto == null || lotto.numOggetti() == 0) throw new IOException("Lotto non valido");
            for(Lotto l: lottiInAsta){
                if(l.getID() == lotto.getID()) throw new IOException("Lotto già presente");
            }
            lottiInAsta.add(lotto);
        }

        public int getID() {
            return CODICE_ASTA;
        }

        public void chiudi(){
            aperta = false;
        }

        public int lottiInAsta(){
            return lottiInAsta.size();
        }

        @Override
        public String toString() {
            String string = CODICE_ASTA + "|" + (aperta? "1":"0") + "\n";
            for(Lotto l: lottiInAsta){
                string += "\t" + l.toString() + "\n";
            }
            return string;
        }
    }
    private List<Asta> aste;

    public GestoreAste(){
        aste = new ArrayList<>();
        deserializza();
    }

    public int creaAsta(){
        Asta asta = new Asta();
        aste.add(asta);
        return asta.getID();
    }

    @Override
    public String toString() {
        int numRighe = aste.size();
        for(Asta a: aste){
            numRighe += a.lottiInAsta();
        }
        String stringa = "" + numRighe + "|\n";
        for(Asta a: aste){
            stringa += a.toString();
        }
        return stringa;
    }

    public void aggiungiLotto(int idAsta, Lotto lotto) throws IOException{
        for(int i = 0; i < aste.size(); i++){
            if(aste.get(i).getID() == idAsta){
                Asta a = aste.get(i);
                a.aggiungiLotto(lotto);
                return;
            }
        }
        throw new IOException("Asta non presente");
    }

    public void serializza(){
        try {
            ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream("aste.bin"));
            output.writeObject(aste);
            output.close();
        } catch (FileNotFoundException e){
            File file = new File("aste.bin");
            try {
                file.createNewFile();
                serializza();
            } catch (IOException ignore) {
            }
        } catch (IOException ignore) {
        }
    }

    public void deserializza(){
        try {
            ObjectInputStream input = new ObjectInputStream(new FileInputStream("aste.bin"));
            try {
                Object obj = input.readObject();
                if(obj instanceof ArrayList<?>){
                    ArrayList<?> a = (ArrayList<?>) obj;
                    if(a.size() > 0){
                        for(Object o : a){
                            if(o instanceof Asta)
                                aste.add((Asta) o);
                        }
                    }
                }
            } catch (ClassNotFoundException ignore) {
            }
            input.close();
        } catch (FileNotFoundException ignore) {
        } catch (IOException ignore) {
        }
    }
}
