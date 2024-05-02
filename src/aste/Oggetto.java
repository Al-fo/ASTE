package aste;

import java.io.IOException;
import java.io.Serializable;

public class Oggetto implements Serializable{
    private final int ID_OGGETTO;
    private static int nextID;
    private String nome, descrizione;
    private CATEGORIE categoria;
    enum CATEGORIE{
        ELETTRONICA(0), ABBIGLIAMENTO(1), ELETTRODOMESTICI(2),
        BAMBINI(3), GIARDINAGGIO(4), SCOLASTICO(5), LAVORO(6), 
        CUCINA(7);

        int valore;
        
        private CATEGORIE(int valore){
            this.valore = valore;
        }

        public static CATEGORIE getCategoriaByValue(int value){
            switch(value){
                case 0:
                    return ELETTRONICA;
                case 1:
                    return ABBIGLIAMENTO;
                case 2:
                    return ELETTRODOMESTICI;
                case 3: 
                    return BAMBINI;
                case 4:
                    return GIARDINAGGIO;
                case 5:
                    return SCOLASTICO;
                case 6: 
                    return LAVORO;
                case 7:
                    return CUCINA;
                default: return null;
            }
        }
    };


    public Oggetto(int categoria, String nome, String descrizione) throws IOException{
        ID_OGGETTO = nextID;
        nextID++;
        this.nome = nome;
        this.descrizione = descrizione;
        this.categoria = CATEGORIE.getCategoriaByValue(categoria);
        if (this.categoria == null) throw new IOException("Categoria non valida");
    }

    public CATEGORIE getCategoria() {
        return categoria;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public int getID() {
        return ID_OGGETTO;
    }
    
    public String getNome() {
        return nome;
    }

    @Override
    public String toString() {
        return getID() + "|" + getNome() + "|" + getDescrizione();
    }
}
