import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * The Server class represents a server that listens for incoming client
 * connections and handles them using individual ClientHandler threads.
 */
public class Server {
    private ServerSocket serverSocket;

    /**
     * Constructs a new Server object.
     * 
     * @param serverSocket The ServerSocket object that listens for incoming
     *                     connections.
     */
    public Server(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    /**
     * The main method of the Server class, used to start the server.
     * 
     * @param args Command-line arguments (not used)
     * @throws IOException For Socket errors.
     */
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(5000);
        Server server = new Server(serverSocket);
        server.startServer();
    }

    /**
     * Starts the server, accepting incoming client connections and creating
     * ClientHandler threads for each connection.
     */
    public void startServer() {
        try {
            while (!serverSocket.isClosed()) {
                Socket socket = serverSocket.accept();
                System.out.println("A new client has been connected");
                ClientHandler clientHandler = new ClientHandler(socket);

                Thread thread = new Thread(clientHandler);
                thread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
            closeServerSocket();
        }
    }

    /**
     * Closes the server's ServerSocket if it is not null.
     */
    public void closeServerSocket() {
        try {
            if (serverSocket != null)
                serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
