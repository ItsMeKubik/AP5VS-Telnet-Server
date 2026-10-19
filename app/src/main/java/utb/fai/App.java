package utb.fai;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;

public class App {

    public static void main(String[] args) {
        if (args.length < 2) {
            System.err.println("Chybí argumenty: <port> <max_threads>");
            return;
        }

        int port = Integer.parseInt(args[0]);
        int maxThreads = Integer.parseInt(args[1]);

        ExecutorService service = Executors.newFixedThreadPool(maxThreads);
        Set<Socket> activeSockets = ConcurrentHashMap.newKeySet();

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server běží na portu: " + serverSocket.getLocalPort());

            while (!service.isShutdown()) {
                Socket socket = serverSocket.accept();
                activeSockets.add(socket);
                System.out.println("Připojil se nový klient: " + socket.getInetAddress());
                
                // Namísto ClientHandler zadání vyžaduje ClientThread
                ClientThread handler = new ClientThread(socket, activeSockets);
                
                try {
                    service.execute(handler);    
                } catch (RejectedExecutionException ex) {
                    System.out.println("Server je přetížený");
                    socket.close();
                    activeSockets.remove(socket);
                }
            }
            
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            for (Socket s : activeSockets) {
                try {
                    s.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            service.shutdownNow();
        }
    }
}