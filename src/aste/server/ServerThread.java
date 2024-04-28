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
    
                            ArrayList<String[]> listaUtenti = leggiUtenti();
                            for(String[] s: listaUtenti){
                                if(s[2].equalsIgnoreCase(email)){
                                    writer.writeBytes("[ER]Esistente\n");
                                    break switchcase;
                                }
                            }
                            listaUtenti.add(new String[5]);
                            listaUtenti.get(listaUtenti.size() - 1)[0] = nome;
                            listaUtenti.get(listaUtenti.size() - 1)[1] = cognome;
                            listaUtenti.get(listaUtenti.size() - 1)[2] = email;
                            listaUtenti.get(listaUtenti.size() - 1)[3] = password;
                            listaUtenti.get(listaUtenti.size() - 1)[4] = tel;
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

                            ArrayList<String[]> listaUtenti = leggiUtenti();
                            for(String[] s: listaUtenti){
                                if(s[2].equals(email)){
                                    if(s[3].equals(password)){
                                        writer.writeBytes("[OK]\n");
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
                    }
                }
        } catch (IOException ignore) {
        }
    }

    private ArrayList<String[]> leggiUtenti() throws IOException{
        BufferedReader fileReader;
        ArrayList<String[]> listaUtenti = new ArrayList<>();
        try{
            fileReader = new BufferedReader(new FileReader(fileUtenti + ".txt"));
            while(fileReader.ready()){
                String[] datiLetti = fileReader.readLine().split("\\|");
                listaUtenti.add(datiLetti);
            }
            fileReader.close();
            return listaUtenti;
        }catch(FileNotFoundException e){
            File file = new File(fileUtenti + ".txt");
            return listaUtenti;
        }
    }

    private void salvaUtenti(ArrayList<String[]> lista) throws IOException{
        BufferedWriter fileWriter = new BufferedWriter(new FileWriter(fileUtenti + ".txt"));
        for(String[] s: lista){
            fileWriter.write(s[0] + "|" + s[1] + "|" + s[2] + "|" + s[3] + "|" + s[4]);
            fileWriter.newLine();
        }
        fileWriter.close();
    }
}   
