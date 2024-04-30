package aste.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;

public class ServerThread extends Thread{
    private static GestoreAste gestoreAste;
    private Socket socket;
    private String fileUtenti = "utenti";

    public ServerThread(Socket socket){
        this.socket = socket;
        gestoreAste = new GestoreAste();
        gestoreAste.deserializza();
        gestoreAste.creaAsta();
        //gestoreAste.
    }

    @Override
    public void run() {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             DataOutputStream writer = new DataOutputStream(socket.getOutputStream())) {
                while(true){
                    System.out.println("Aspetto di leggere");
                    String ricevuto = reader.readLine();
                    System.out.println("ho letto" + ricevuto);
                    String comando = ricevuto.substring(0,ricevuto.indexOf("|"));
                    System.out.println("ho ricavato: " + comando);
                    switchcase: switch(comando){
                        case "-Registra":{
                            String[] dati = new String[5];
                            Arrays.fill(dati, "");
                            int j = 0;
                            for(int i = comando.length() + 1; i < ricevuto.length(); i++){
                                if(ricevuto.charAt(i) != '|'){
                                    dati[j] += ricevuto.charAt(i);
                                }
                                else j++;
                            }
                            String nome = dati[0];
                            String cognome = dati[1];
                            String email = dati[2];
                            String password = dati[3];
                            String tel = dati[4];

                            //TODO: Controlli sui dati
    
                            System.out.println("dati ricevuti");
    
                            ArrayList<Utente> listaUtenti = leggiUtenti();
                            for(Utente u: listaUtenti){
                                if(u.getEmail().equalsIgnoreCase(email)){
                                    writer.writeBytes("[ER]Esistente\n");
                                    break switchcase;
                                }
                            }
                            listaUtenti.add(new Utente(nome, cognome, email, password, tel));
                            writer.writeBytes("[OK]\n");
                            salvaUtenti(listaUtenti);
                            break;
                        }
                        case "-Login":{
                            String[] dati = new String[2];
                            Arrays.fill(dati, "");
                            int j = 0;
                            for(int i = comando.length() + 1; i < ricevuto.length(); i++){
                                if(ricevuto.charAt(i) != '|'){
                                    dati[j] += ricevuto.charAt(i);
                                }
                                else j++;
                            }

                            String email = dati[0];
                            String password = dati[1];

                            System.out.println(email + " " + password);

                            ArrayList<Utente> listaUtenti = leggiUtenti();
                            for(Utente u: listaUtenti){
                                if(u.getEmail().equals(email)){
                                    if(u.getPassword().equals(password)){
                                        if(u.isConnected()){
                                            writer.writeBytes("[ER]Connected");
                                            break switchcase;
                                        }
                                        writer.writeBytes("[OK]" + u.getID() + "\n");
                                        u.connect();
                                        break switchcase;
                                    }else{
                                        writer.writeBytes("[ER]Dati\n");
                                        break switchcase;
                                    }
                                }
                            }
                            writer.writeBytes("[ER]Dati\n");
                            break;
                        }
                        case "-Logout":{
                            int id = Integer.parseInt(ricevuto.substring(comando.length() + 1));
                            ArrayList<Utente> listaUtenti = leggiUtenti();
                            for(int i = 0; i < listaUtenti.size(); i++){
                                Utente u = listaUtenti.get(i);
                                if(u.getID() == id){
                                    if(u.isConnected()){
                                        u.disconnect();
                                        listaUtenti.set(i,u);
                                        writer.writeBytes("[OK]\n");
                                        break switchcase;
                                    }else{
                                        writer.writeBytes("[ER]Connesso\n");
                                        break switchcase;
                                    }
                                }
                            }
                            writer.writeBytes("[ER]Utente\n");
                            break;
                        }
                        case "-Richiesta":{
                            int id = Integer.parseInt(ricevuto.substring(comando.length() + 1));
                            ArrayList<Utente> listaUtenti = leggiUtenti();
                            for(int i = 0; i < listaUtenti.size(); i++){
                                Utente u = listaUtenti.get(i);
                                if(u.getID() == id){
                                    if(!u.isConnected()){
                                        writer.writeBytes("-1|Non Connesso\n");
                                        break switchcase;
                                    }
                                    writer.writeBytes(gestoreAste.toString());
                                }
                            }
                        }
                    }
                }
        } catch (IOException ignore) {
        }
    }

    private ArrayList<Utente> leggiUtenti() throws IOException{
        BufferedReader fileReader;
        ArrayList<String[]> listaStringhe = new ArrayList<>();
        ArrayList<Utente> listaUtenti = new ArrayList<>();
        try{
            fileReader = new BufferedReader(new FileReader(fileUtenti + ".txt"));
            while(fileReader.ready()){
                String[] datiLetti = fileReader.readLine().split("\\|");
                listaStringhe.add(datiLetti);
            }
            fileReader.close();
            for(String[] s: listaStringhe){
                listaUtenti.add(new Utente(s[0], s[1], s[2], s[3], s[4], s[5], (s[6].equals("1"))));
            }
            return listaUtenti;
        }catch(FileNotFoundException e){
            File file = new File(fileUtenti + ".txt");
            return listaUtenti;
        }
    }

    private void salvaUtenti(ArrayList<Utente> lista) throws IOException{
        BufferedWriter fileWriter = new BufferedWriter(new FileWriter(fileUtenti + ".txt"));
        for(Utente u: lista){
            fileWriter.write(u.toString());
            fileWriter.newLine();
        }
        fileWriter.close();
    }
}   
