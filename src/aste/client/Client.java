package aste.client;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class Client {
    private static int porta = 3000;
    private static boolean loggedIn = false;
    public static void main(String[] args) {
        try (Socket socket = new Socket("localhost", porta);
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            DataOutputStream writer = new DataOutputStream(socket.getOutputStream())) {
                Scanner scanner = new Scanner(System.in);
                int scelta = 0;
                int id = -1;
                connessione: while(true){
                    while(!loggedIn && scelta >= 0 && scelta <= 1){
                        System.out.println("0: Registrati\n1: Login\n2: Esci");
                        scelta = scanner.nextInt();
                        scanner.nextLine();
                        if(scelta == 2){
                            loggedIn = false;
                            break connessione;
                        } 
                        switch(scelta){
                            case 0:{
                                System.out.println("Inserisci Nome, Cognome, email, password, numero di telefono");
                                String nome = scanner.nextLine();
                                String cognome = scanner.nextLine();
                                String email = scanner.nextLine();
                                String password = scanner.nextLine();
                                String tel = scanner.nextLine();
                                writer.writeBytes("-Registra|" + nome + "|" + cognome + "|" + 
                                                   email + "|" + password + "|" + tel + "\n");
                                String risposta;
    
                                risposta = reader.readLine();
                                if(risposta.contains("[OK]")){
                                    System.out.println("Registrazione avvenuta con successo");
                                }else{
                                    switch(risposta.substring(4).trim()){
                                        case "Esistente":
                                            System.out.println("L'utente è già registrato");
                                            break;
                                        case "Dati":
                                            System.out.println("Errore nei dati inseriti");
                                            break;
                                        default:
                                            System.out.println("Errore, si prega di riprovare");
                                            break;
                                    }
                                }
                                break;
                            }
                            case 1:{
                                System.out.println("Inserisci email, password");
                                String email = scanner.nextLine();
                                String password = scanner.nextLine();
                                writer.writeBytes("-Login|" + email + "|" + password + "\n");
                                String risposta;
                                risposta = reader.readLine();
                                if(risposta.contains("[OK]")){
                                    id = Integer.parseInt(risposta.substring(4));
                                    System.out.println("Login avvenuto con successo");
                                    loggedIn = true;
                                }else{
                                    switch(risposta.substring(4).trim()){
                                        case "Dati":
                                            System.out.println("Errore nei dati inseriti");
                                            break;
                                        case "Connected":
                                            System.out.println("Errore, l'utente è già connesso");
                                            break;
                                        default:
                                            System.out.println("Errore, si prega di riprovare");
                                            break;
                                    }
                                }
                                break;
                            }
                            default:
                                break;
                        }
                    }
                    while(loggedIn){
                        if(id == -1){
                            System.out.println("Errore, riprovare il login");
                            scelta = 0;
                        }else{
                            System.out.println("0: Logout\n1: Richiedi lista aste\n2: Crea lotto\n3: Inserisci lotto\n" + 
                            "4: Visualizza oggetto\n5: Effettua puntata");
                            scelta = scanner.nextInt();
                            scanner.nextLine();
                        }

                        if(scelta == 0){
                            disconnessione:{
                                if(id != -1){
                                    writer.writeBytes("-Logout|" + id + "\n");
                                    String risposta = reader.readLine();
                                    if(risposta.contains("[OK]")){
                                        System.out.println("Logout avvenuto con successo");
                                    }else{
                                        switch (risposta.substring(4)) {
                                            case "Connesso": case "Utente":
                                                System.out.println("Pare che tu non sia connesso");
                                                break disconnessione;
                                            default:
                                                System.out.println("è avvenuto un problema si prega di riprovare");
                                                break disconnessione;
                                        }
                                    }
                                }
                                loggedIn = false;
                            }
                            break;
                        }
                        switch(scelta){
                            case 1:{
                                writer.writeBytes("-Richiesta|\n");
                                String risposta;
                                risposta = reader.readLine();
                                if(risposta.contains("[OK]")){
                                    int numeroRighe;
                                    numeroRighe = Integer.parseInt(risposta.substring(5,risposta.indexOf("|")));
                                    for(int i = 0; i < numeroRighe; i++){
                                        System.out.println(reader.readLine());
                                    }
                                }else{
                                    System.out.println("Errore, si prega di riprovare");
                                }
                                break;
                            }
                            //TODO: Altri casi di scelta
                        }
                    }
                }
                scanner.close();
        } catch (IOException ignore) {
        }
    }   
}
