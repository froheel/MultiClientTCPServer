import java.io.*;
import java.net.Socket;

public class Client {

    // Attributes
    private static final String SERVER_IP = "127.0.0.1";
    private static final int SERVER_PORT = 9090;

    /**
     * creates a file and writes data to it
     * @param filename
     * @param data
     * @throws IOException
     */
    public static synchronized void createFile(String filename, String data) throws IOException {
        File file = new File(filename);

        // Checking if file already exists
        if (!file.exists()) {
            file.createNewFile();
        }
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(filename,false));

        bufferedWriter.write(data);

        //Closing connections
        bufferedWriter.close();
    }

    /**
     * Main method that is the driver and communicates with the server
     * @param args
     */
    public static void main(String[] args){

        Socket socket = null;
        BufferedReader keyboard = null;
        PrintWriter outToServer = null;
        BufferedReader inFromServer = null;

        try {

            // Intialization
            socket = new Socket(SERVER_IP, SERVER_PORT);

            keyboard = new BufferedReader(new InputStreamReader(System.in));
            outToServer = new PrintWriter(socket.getOutputStream(), true);
            inFromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            System.out.println("Client " + socket.getInetAddress().getHostAddress() + " is active");

            //read welcome from server
            String welcome = "";
            while (welcome =="" || welcome == null) {
                welcome = inFromServer.readLine();
                welcome = welcome.replaceAll("\\r |\\n", "");
            }
            System.out.println(welcome);

            // Check if client wants to upload data or read data
            System.out.println("enter u to upload data or r to read data");


            while(true){

                Character command = (char)keyboard.read();

                if(command.equals('u')){

                    // Send data to server
                    System.out.println("Enter text to send to the Server");

                    // Notify server
                    outToServer.println("upload");

                    // Enter data to upload
                    keyboard.readLine();
                    String data = "";
                    while( data == "" || data == null) {
                        data = keyboard.readLine();
                        data = data.replaceFirst("\\r|\\n", "");
                    }

                    //output to sever
                    outToServer.println(data);
                    outToServer.flush();

                }
                else if (command.equals('r')){

                    // Read Data from sever
                    System.out.println("Reading data from the Server");

                    // Notify the Server that client wants to read
                    outToServer.println("read");

                    // Read the data sent by the server
                    String data = "";
                    while (data =="" || data == null) {
                        data = inFromServer.readLine();
                        data = data.replaceAll("\\r |\\n", "");
                    }
                    System.out.println("[Data from Server]: " + data);

                    if(data.contains("No")){
                        //no information found so no need to store data
                    }
                    else{
                        String filename = socket.getInetAddress().getHostAddress()+"_"+ welcome.substring(18,31) +".txt";
                        createFile(filename, data);
                    }

                }
                else if(command.equals('\n')){

                    //ignore the new line character
                }
                else{

                    System.out.println("This is not a valid command");
                }

            }


        }
        catch (IOException e) {
            e.getMessage();
        }
        catch(Exception e){
            e.getMessage();
        }
        finally{

            // closing connections
            if( socket != null){
                try {
                    socket.close();
                } catch (IOException e) {
                    e.getMessage();
                }
            }

            if( keyboard != null){
                try {
                    keyboard.close();
                } catch (IOException e) {
                    e.getMessage();
                }
            }

            if( outToServer != null){
                outToServer.close();
            }

            if(inFromServer!=null){
                try {
                    inFromServer.close();
                }
                catch (IOException e){
                    e.getMessage();
                }
            }

        }


    }

}
