package aste.server;

import java.io.IOException;
import java.net.ServerSocket;

public class Server {
    private static int porta = 3000;
    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(porta)) {
            while(true){
                new ServerThread(serverSocket.accept()).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
