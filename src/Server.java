import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

    // private attributes
    private static int threads = 5;
    private static final int PORT = 9090;
    private static ExecutorService pool = Executors.newFixedThreadPool(threads);
    private static int count = 0;


    /**
     * increments active connections
     */
    public static synchronized void increment(){
        count++;
        System.out.println("Active Connections = " + count);
    }

    /**
     * decrements active connections
     */
    public static synchronized void decrement(ServerSocket listener){
        count--;
        System.out.println("Active Connections = " + count);
        if(count == 0){
            System.out.println("Waiting for client connection on port: " + listener.getLocalPort());
        }
    }

    /**
     * Server creates threads to handle multiple clients and processes accordingly
     * @param args
     */
    public static void main(String[] args) {

        ServerSocket listener = null;

        try {

            // Waiting
            listener = new ServerSocket(PORT);
            System.out.println("Waiting for client connection on port: " + listener.getLocalPort());

            while (true) {
                // Accept Connection
                Socket client = listener.accept();
                System.out.println("Got connection from " + client.getInetAddress().getHostAddress() + " : " + client.getPort());
                increment();

                // Create an instance of ClientHandle and execute it
                ClientHandler clientThread = new ClientHandler(client, listener);
                Thread t  = new Thread(clientThread);
                t.start();
                //pool.execute(clientThread);
            }
        }
        catch (IOException e){
            e.getMessage();
        }
        catch (Exception e){
            e.getMessage();
        }
        finally {
            if(listener!= null){
                try {
                    listener.close();
                } catch (IOException e) {
                    e.getMessage();
                }
            }
        }

    }
}
