package utb.fai;

import java.io.*;
import java.net.*;
import java.util.Set;

public class ClientThread implements Runnable {

    private Socket clientSocket;
    private final Set<Socket> activeSockets;

    public ClientThread(Socket clientSocket,Set<Socket> activeSockets) {
        this.clientSocket = clientSocket;
        this.activeSockets = activeSockets;
    }

    @Override
    public void run() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(),true);


            String line;
            while ((line = in.readLine()) != null) {
                System.out.println("Klient (" + clientSocket.getInetAddress() + ") napsal: " + line);
                out.println(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                this.clientSocket.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
            this.activeSockets.remove(clientSocket);
            System.out.println("Klient (" + this.clientSocket.getInetAddress() + ") byl odpojen!");
        }

    }
}
