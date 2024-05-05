package aste.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class GestoreAste implements Serializable{
	final static long serialVersionUID = 319310233;
    public class Asta implements Serializable{
        final static long serialVersionUID = 319310233;
        private boolean aperta;
        private final int CODICE_ASTA;
        private List<Lotto> lottiInAsta;

        public Asta(){
            int nextCodice = Integer.MAX_VALUE;
            BufferedWriter writer;
            BufferedReader reader;

            try{
                reader = new BufferedReader(new FileReader("nextCodici.txt"));
                nextCodice = Integer.parseInt(reader.readLine());
                int codiceLotto = Integer.parseInt(reader.readLine());
                int codiceOggetto = Integer.parseInt(reader.readLine());
                reader.close();
                writer = new BufferedWriter(new FileWriter("nextCodici.txt"));
                writer.write(Integer.toString((nextCodice + 1)) + "\n");
                writer.append(Integer.toString(codiceLotto) + "\n");
                writer.append(Integer.toString(codiceOggetto));
                writer.close();
            }catch(IOException ignore){
            }
            
            aperta = true;
            lottiInAsta = new ArrayList<>();
            CODICE_ASTA = nextCodice;
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

        public void replaceLotto(Lotto l, int idLotto) throws IOException{
            for(Lotto ll: lottiInAsta){
                if(ll.getID() == idLotto){
                    int index = lottiInAsta.indexOf(ll);
                    lottiInAsta.set(index, l);
                }
            }
            throw new IOException("Lotto non presente");
        }

        public Lotto getLotto(int idLotto) throws IOException{
            for(Lotto l: lottiInAsta){
                if(l.getID() == idLotto) return l;
            }
            throw new IOException("Lotto non presente");
        }

        public boolean isAperta() {
            return aperta;
        }

        @Override
        public String toString() {
            String string = "ID asta: " + CODICE_ASTA + "|Aperta: " + (aperta? "Si":"No") + "\n";
            for(Lotto l: lottiInAsta){
                string += "\t" + l.toString() + "\n";
            }
            return string;
        }
    }
    private List<Asta> aste;

    public GestoreAste(){
        aste = new ArrayList<>();
    }

    public void chiudiAsta(int idAsta) throws IOException{
        for(Asta a: aste){
            if(a.getID() == idAsta){
                int index = aste.indexOf(a);
                a.chiudi();
                aste.set(index, a);
                return;
            }
        }
        throw new IOException("Asta non presente");
    }

    public void replace(Asta a, int idAsta) throws IOException{
        for(int i = 0; i < aste.size(); i++){
            if(aste.get(i).getID() == idAsta){
                aste.set(i,a);
                return;
            }
        }
        throw new IOException("Id non valido");
    }

    public List<Asta> getAste() {
        return aste;
    }

    public Asta getAsta(int idAsta) throws IOException{
        for(int i = 0; i < aste.size(); i++){
            if(aste.get(i).getID() == idAsta) return aste.get(i);
        }
        throw new IOException("Asta non presente");
    }

    public int quantitaAste(){
        return aste.size();
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

    public synchronized void aggiungiLotto(int idAsta, Lotto lotto) throws IOException{
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
            FileInputStream fileInput = null;
            try{
                fileInput = new FileInputStream("aste.bin");
            }catch(IOException ignore){
            }
            ObjectInputStream input = new ObjectInputStream(fileInput);
            try {
                Object obj = input.readObject();
                if(obj instanceof ArrayList<?>){
                    ArrayList<?> a = (ArrayList<?>) obj;
                    if(a.size() > 0){
                        for(Object o : a){
                            if(o instanceof Asta)
                                aste.add((Asta) o);
                                System.out.println("Asta aggiunta " + o.toString());
                        }
                    }
                }
            } catch (ClassNotFoundException ignore) {
                System.err.println(ignore.getMessage());
            }catch(IOException e){
                System.out.println(e.getMessage());
            }
            input.close();
        } catch (FileNotFoundException ignore) {
            System.err.println(ignore.getMessage());
        } catch (IOException ignore) {
            System.err.println(ignore.getMessage());
        }
    }
}
