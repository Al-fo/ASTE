package aste.client;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;
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
                ArrayList<ThreadMulticast> threadPool = new ArrayList<>();
                boolean admin = false;
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
                                if(risposta.contains("[OK]") || risposta.contains("[AD]")){
                                    id = Integer.parseInt(risposta.substring(4));
                                    System.out.println("Login avvenuto con successo");
                                    loggedIn = true;
                                    if(risposta.contains("[AD]")){
                                        System.out.println("Account admin attivo");
                                        admin = true;
                                    } 
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
                    if(admin){
                        while(loggedIn){
                            if(id == -1){
                                System.out.println("Errore, riprovare il login");
                                admin = false;
                                scelta = 0;
                            }else{
                                System.out.println("0: Logout\n1: Richiedi lista aste\n" + "2: Crea asta\n" + "3: Chiudi asta");
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
                                            admin = false;
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
                                    writer.writeBytes("-Richiesta|" + id + "\n");
                                    String risposta;
                                    risposta = reader.readLine();
                                    if(risposta.contains("[OK]")){
                                        int numeroRighe;
                                        numeroRighe = Integer.parseInt(risposta.substring(4,risposta.indexOf("|")));
                                        for(int i = 0; i <= numeroRighe; i++){
                                            System.out.println(reader.readLine());
                                        }
                                    }else{
                                        switch (risposta.substring(4)) {
                                            case "Non connesso":
                                                System.out.println("Errore, connessione scaduta");
                                                id = -1;
                                                break;
                                        
                                            default:
                                                System.out.println("Errore, si prega di riprovare");
                                                break;
                                        }
                                    }
                                    break;
                                }
                                case 2:{
                                    writer.writeBytes("-Crea|" + id + "\n");
                                    String risposta;
                                    risposta = reader.readLine();
                                    if(risposta.contains("[OK]")){
                                        System.out.println("Asta creata con successo");
                                    }else{
                                        switch(risposta.substring(4)){
                                            case "NoAdmin":
                                                System.out.println("Errore, account non admin");
                                                id = -1;
                                                break;
                                            case "Non connesso":
                                                System.out.println("Utente non connesso, riprovare");
                                                id = -1;
                                                break;
                                            default:
                                                System.out.println("Errore, riprovare");
                                        }
                                    }
                                    break;
                                }
                                case 3:{
                                    writer.writeBytes("-Chiudi|" + id + "\n");
                                    String risposta;
                                    risposta = reader.readLine();
                                    if(risposta.contains("[OK]")){
                                        System.out.println("Inserisci l'id dell'asta da chiudere");
                                        int idAsta = scanner.nextInt();
                                        scanner.nextLine();
                                        writer.writeBytes(idAsta + "\n");
                                        risposta = reader.readLine();
                                        if(risposta.contains("[OK]")){
                                            System.out.println("Asta chiusa con successo");
                                            break;
                                        }else{
                                            switch (risposta.substring(4)) {
                                                case "Asta non presente":{
                                                    System.out.println("L'asta indicata non è presente");
                                                    break;
                                                }
                                                case "Utente non trovato":{
                                                    System.out.println("Errore, l'utente non è stato trovato, ripetere il login");
                                                    id = -1;
                                                    break;
                                                }
                                                default:
                                                    System.out.println("Errore, si prega di riprovare");
                                                    break;
                                            }
                                        }
                                    }else{
                                        switch (risposta.substring(4)) {
                                            case "Non connesso":
                                                System.out.println("Errore, l'utente non risulta connesso");
                                                id = -1;
                                                break;
                                            case "NoAdmin":{
                                                System.out.println("Errore, l'utente connesso non è un admin");
                                                break;
                                            }
                                            default:
                                                System.out.println("Errore, si prega di riprovare");
                                                break;
                                        }
                                    }
                                    break;
                                }
                            }
                        }
                    }else{
                        while(loggedIn){
                            if(id == -1){
                                System.out.println("Errore, riprovare il login");
                                scelta = 0;
                            }else{
                                System.out.println("0: Logout\n1: Richiedi lista aste\n2: Inserisci lotto\n" + 
                                "3: Entra gruppo lotto\n4: Esci gruppo lotto\n5: Effettua rilancio");
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
                                    writer.writeBytes("-Richiesta|" + id + "\n");
                                    String risposta;
                                    risposta = reader.readLine();
                                    if(risposta.contains("[OK]")){
                                        int numeroRighe;
                                        numeroRighe = Integer.parseInt(risposta.substring(4,risposta.indexOf("|")));
                                        for(int i = 0; i <= numeroRighe; i++){
                                            System.out.println(reader.readLine());
                                        }
                                    }else{
                                        switch (risposta.substring(4)) {
                                            case "Non connesso":
                                                System.out.println("Errore, connessione scaduta");
                                                id = -1;
                                                break;
                                        
                                            default:
                                                System.out.println("Errore, si prega di riprovare");
                                                break;
                                        }
                                    }
                                    break;
                                }
                                case 2:{
                                    writer.writeBytes("-Inserimento|" + id + "\n");
                                    String risposta = reader.readLine();
                                    if(risposta.contains("[OK]")){
                                        System.out.println("Inserisci la quantità di oggetti da inserire");
                                        int numOggetti = scanner.nextInt();
                                        scanner.nextLine();
                                        System.out.println("Inserisci l'id dell'asta in cui inserire il lotto");
                                        int idAsta = scanner.nextInt();
                                        scanner.nextLine();
                                        writer.writeBytes(numOggetti + "|" + idAsta + "\n");
                                        risposta = reader.readLine();
                                        if(risposta.contains("[OK]")){
                                            System.out.println("Inserisci i dati degli oggetti da inserire ovvero: \n" +
                                            "Categoria{0: Elettronica, 1: Abbigliamento, 2: Elettrodomestica\n" +
                                            "3: Bambini, 4: Giardinaggio, 5: Scuola, 6: Lavoro, 7: Cucina}\n"+   
                                            "Nome dell'oggetto\nBreve descrizione dell'oggetto\nper " + numOggetti + " oggetti");
                                            int categoria = -1;
                                            String nome = "";
                                            String desc = "";
                                            for(int i = 0; i < numOggetti; i++){
                                                categoria = scanner.nextInt();
                                                scanner.nextLine();
                                                nome = scanner.nextLine();
                                                desc = scanner.nextLine();
                                                writer.writeBytes(categoria + "|" + nome + "|" + desc + "\n");
                                            }
    
                                            risposta = reader.readLine();
                                            if(risposta.contains("[OK]")){
                                                System.out.println("Inserisci un nome per il lotto, il prezzo base ed' il rilancio minimo");
                                                nome = scanner.nextLine();
                                                double prezzoBase = scanner.nextDouble();
                                                scanner.nextLine();
                                                double rilancioMinimo = scanner.nextDouble();
                                                scanner.nextLine();
        
                                                writer.writeBytes(nome + "|" + (double) prezzoBase + "|" + (double) rilancioMinimo + "\n");
    
                                                risposta = reader.readLine();
                                                if(!risposta.contains("[OK]")){
                                                    System.out.println("Errore, si è verificato un errore");
                                                    break;
                                                }
                                            }else{
                                                switch(risposta.substring(4)){
                                                    case "Categoria non valida":
                                                        System.out.println("Errore, categoria inserita non valida");
                                                        break;
                                                    default:
                                                        System.out.println("Errore, riprovare");
                                                        break;
                                                }
                                            }
                                        }else{
                                            switch(risposta.substring(4)){
                                                case "Troppi oggetti":
                                                    System.out.println("Errore, il massimo di oggetti di un lotto è 4");
                                                    break;
                                                case "Id asta errato":
                                                    System.out.println("Errore, Id asta inserito non valido");
                                                    break;
                                                case "Asta chiusa":
                                                    System.out.println("Errore, asta chiusa");
                                                    break;
                                                default:
                                                    System.out.println("Errore, riprovare");
                                                    break;
                                            }
                                        }
                                    }else{
                                        switch(risposta.substring(4)){
                                            case "Non connesso":
                                                System.out.println("Errore, utente non connesso");
                                                id = -1;
                                                break;
                                            default:
                                                System.out.println("Errore");
                                                id = -1;
                                                break;
                                        }
                                    }
                                    break;
                                }
                                case 3:{
                                    joingroup:{
                                        System.out.println("inserisci indirizzo multicast del gruppo in cui entrare");
                                        String indirizzo;
                                        indirizzo = scanner.nextLine();
                                        try{
                                            for(ThreadMulticast t: threadPool){
                                                if(t.getIndirizzo().toString().contains(indirizzo)){
                                                    System.out.println("Fai già parte del gruppo");
                                                    break joingroup;
                                                }
                                            }
                                            ThreadMulticast thread = new ThreadMulticast(indirizzo);
                                            thread.start();
                                            threadPool.add(thread);
                                        }catch(UnknownHostException e){
                                            System.out.println("Errore nell'indirizzo inserito");
                                        }
                                    }
                                    break;
                                }
                                case 4:{
                                    leavegroup:{
                                        System.out.println("Inserisci l'ip del gruppo da cui uscire");
                                        String indirizzo;
                                        indirizzo = scanner.nextLine();
                                        for(int i = 0; i < threadPool.size(); i++){
                                            ThreadMulticast t = threadPool.get(i);
                                            if(t.getIndirizzo().toString().contains(indirizzo)){
                                                t.interrupt();
                                                break leavegroup;
                                            }
                                        }
                                        System.out.println("Errore, non fai parte del gruppo voluto");
                                    }
                                    break;
                                }
                                case 5:{
                                    writer.writeBytes("-Punta|" + id + "\n");
                                    String risposta;
                                    risposta = reader.readLine();
                                    if(risposta.contains("[OK]")){
                                        System.out.println("Inserisci l'id (ultimo punto) del lotto su cui rilanciare");
                                        int idLotto = scanner.nextInt();
                                        scanner.nextLine();
                                        System.out.println("Inserisci l'id dell'asta su cui si trova il lotto");
                                        int idAsta = scanner.nextInt();
                                        scanner.nextLine();
                                        System.out.println("Inserisci quanto rilanciare sul lotto");
                                        double rilancio = scanner.nextDouble();
                                        scanner.nextLine();
                                        writer.writeBytes(idLotto + "|" + idAsta + "|" + rilancio + "\n");
                                        risposta = reader.readLine();
                                        if(risposta.contains("[OK]")){
                                            System.out.println("Rilancio efettuato con successo");
                                            break;
                                        }else{
                                            switch(risposta.substring(4)){
                                                case "AstaLotto":
                                                    System.out.println("Errore, Id asta o Lotto errato");
                                                    break;
                                                default:
                                                    System.out.println(risposta.substring(4));
                                            }
                                        }
                                    }else{
                                        switch (risposta.substring(4)) {
                                            case "Non connesso":
                                                System.out.println("Errore, utente non connesso");
                                                id = -1;
                                                break;
                                            case "Utente non presente":
                                                System.out.println("Errore, si prega di riprovare il login");
                                                id = -1;
                                                break;
                                        }
                                    }
                                }
                            }
                        }
                    }
                    
                }
                scanner.close();
        } catch (IOException ignore) {
        }
    }   
}
