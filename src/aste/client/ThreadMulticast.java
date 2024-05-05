package aste.client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;

public class ThreadMulticast extends Thread{
    private InetAddress indirizzo;
    private static final int porta = 3200;

    public ThreadMulticast(String indirizzo) throws UnknownHostException{
        this.indirizzo = InetAddress.getByName(indirizzo);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void run() {
        try (MulticastSocket socket = new MulticastSocket(porta)) {
            byte[] buffer = new byte[1024];
            socket.joinGroup(indirizzo);
            while(true){
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);
                String msg = new String(packet.getData());
                System.out.println("Messaggio da " + packet.getAddress().toString() + ": " + msg);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public InetAddress getIndirizzo() {
        return indirizzo;
    }
}
