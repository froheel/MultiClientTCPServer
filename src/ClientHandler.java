import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class ClientHandler implements Runnable {

    // private attributes
    private Socket client;
    private BufferedReader inFromClient;
    private PrintWriter outToClient;
    private ServerSocket server;

    /**
     * @param clientSocket
     * @param server
     * @throws IOException
     */
    public ClientHandler(Socket clientSocket, ServerSocket server) throws IOException {
        this.client = clientSocket;
        this.server = server;
        inFromClient = new BufferedReader(new InputStreamReader(client.getInputStream()));
        outToClient = new PrintWriter(client.getOutputStream(), true);

    }

    /**
     * uploads data to the file; appends if file exists else creates new file and stores in ir
     * @param filename
     * @param data
     * @throws IOException
     */
    public static synchronized void uploadtoFile(String filename, String data) throws IOException {
        File file = new File(filename + ".txt");

        // Checking if file already exists
        if (!file.exists()) {
            file.createNewFile();
        }
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(filename+".txt",true));

        bufferedWriter.write(data);

        //Closing connections
        bufferedWriter.close();
    }

    /**
     * reads from file and returns data
     * @param filename
     * @return "" if empty else returns file data
     */
    public static synchronized String readfromFile(String filename) throws IOException {
        String data = "";
        File file = new File(filename +".txt");
        if(!file.exists()){
            return data;
        }
        else{
            // Read from file
            BufferedReader br = new BufferedReader(new FileReader(file));
            String st;
            while(( st = br.readLine()) != null){
                data = data.concat(st);
            }

            //closing connection
            br.close();
            return data;
        }
    }

    /**
     * Main method that gives response to the Client and processes the data if required
     */
    @Override
    public void run(){
        try{

            // Welcome the Client
            String welcome = "Welcome to server " + InetAddress.getLocalHost().getHostAddress() + " : " + server.getLocalPort();
            System.out.println(welcome);
            outToClient.println(welcome);


            while (true){
                String request = inFromClient.readLine();

                if(request.contains("upload")){
                    String dataToUpload = "";
                    while(dataToUpload=="" || dataToUpload ==null) {
                        dataToUpload = inFromClient.readLine();
                        dataToUpload = dataToUpload.replaceFirst("\\r|\\n", "");
                    }
                    System.out.println("Client data: " + dataToUpload);
                    uploadtoFile(client.getInetAddress().getHostName(),dataToUpload);
                    System.out.println("Information saved for client : " + client.getInetAddress().getHostAddress());
                }
                else if (request.contains("read")){

                    String fileText = readfromFile(client.getInetAddress().getHostAddress());
                    if(fileText==""){
                        //does not exist
                        System.out.println("No Information found for client " + client.getInetAddress().getHostAddress());
                        outToClient.println("No Information found");
                    }
                    else{
                        //file exists
                        System.out.println("Information for client " + client.getInetAddress().getHostAddress());
                        System.out.println("File content: " + fileText);
                        outToClient.println(fileText);
                    }


                }
                else{
                    // do nothing as invalid command
                    System.out.println("Invalid command from: " + client.getInetAddress().getHostAddress());
                }

            }

        } catch (IOException e) {
            e.getMessage();
            System.err.println("exception in client handler");

        } finally {

            try {
                client.close();
            } catch (IOException e) {
                e.getMessage();
            }

            // Remove and decrement the number of connections.
            Server.decrement(server);

            outToClient.close();

            try {
                inFromClient.close();
            } catch (IOException e) {
                e.getMessage();
            }
        }

    }

}
