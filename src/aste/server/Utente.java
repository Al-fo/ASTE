package aste.server;

public class Utente {
    public final int ID;
    private static int nextID = 0;
    private String nome;
    private String cognome;
    private String email;
    private String password;
    private String telefono;
    private boolean connected;
    private boolean admin;

    public Utente(String nome, String cognome, String email, String password, String telefono){
        ID = nextID;
        nextID++;
        this.nome = nome;
        this.cognome = cognome;
        this.email = email;
        this.password = password;
        this.telefono = telefono;
        connected = false;
        admin = false;
    }

    public Utente(String ID, String nome, String cognome, String email, String password, String telefono, boolean connected, boolean admin){
        this.ID = Integer.parseInt(ID);
        this.nome = nome;
        this.cognome = cognome;
        this.email = email;
        this.password = password;
        this.telefono = telefono;
        this.connected = connected;
        this.admin = admin;
    }

    public boolean isAdmin() {
        return admin;
    }

    public int getID() {
        return ID;
    }

    public String getNome(){
        return nome;
    }

    public String getCognome(){
        return cognome;
    }

    public String getEmail(){
        return email;
    }

    public String getPassword(){
        return password;
    }

    public String getTelefono() {
        return telefono;
    }

    public boolean isConnected() {
        return connected;
    }

    public void connect(){
        connected = true;
    }

    public void disconnect(){
        connected = false;
    }
    @Override
    public String toString() {
        return ID + "|" + nome + "|" + cognome + "|" + email + "|" + password + "|" + telefono + "|" + (isConnected()? "1":"0") + "|" + (admin? "1":"0");
    }

}
