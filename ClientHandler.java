import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;

/**
 * The ClientHandler class represents a handler for individual client
 * connections in a multi-client chat server.
 * Each client handler manages communication with a single client, including
 * receiving and broadcasting messages.
 */
public class ClientHandler implements Runnable {
    /**
     * A list of all active ClientHandler instances, representing connected clients.
     */
    public static ArrayList<ClientHandler> clientHandlers = new ArrayList<>();

    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String clientUserName;

    /**
     * Constructs a new ClientHandler object for a connected client.
     * 
     * @param socket The Socket object representing the client's connection.
     */
    public ClientHandler(Socket socket) {
        try {
            this.socket = socket;
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.clientUserName = bufferedReader.readLine();

            clientHandlers.add(this);
            broadCastMessage("SERVER: " + clientUserName + " has entered the chat room.");
        } catch (IOException e) {
            closeAll(socket, bufferedReader, bufferedWriter);
        }
    }

    /**
     * The run method of the Runnable interface. Listens for messages from the
     * client and broadcasts them to all clients.
     */
    @Override
    public void run() {
        String messageFromClient;
        while (socket.isConnected()) {
            try {
                messageFromClient = bufferedReader.readLine();
                broadCastMessage(messageFromClient);
            } catch (IOException e) {
                closeAll(socket, bufferedReader, bufferedWriter);
                break;
            }
        }
    }

    /**
     * Broadcasts a message to all connected clients, excluding the sender.
     * 
     * @param messageToSend The message to broadcast.
     */
    public void broadCastMessage(String messageToSend) {
        for (ClientHandler clientHandler : clientHandlers) {
            try {
                if (!clientHandler.clientUserName.equals(this.clientUserName)) {
                    clientHandler.bufferedWriter.write(messageToSend);
                    clientHandler.bufferedWriter.newLine();
                    clientHandler.bufferedWriter.flush();
                }
            } catch (IOException e) {
                closeAll(socket, bufferedReader, bufferedWriter);
            }
        }
    }

    /**
     * Removes the client handler from the list of active handlers and broadcasts
     * their departure.
     */
    public void removeClientHandler() {
        clientHandlers.remove(this);
        broadCastMessage("SERVER: " + clientUserName + "has left the chat room.");
    }

    /**
     * Closes the socket, BufferedReader, and BufferedWriter associated with this
     * client handler.
     * 
     * @param socket         The Socket to close.
     * @param bufferedReader The BufferedReader to close.
     * @param bufferedWriter The BufferedWriter to close.
     */
    public void closeAll(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
        removeClientHandler();
        try {
            if (bufferedReader != null) {
                bufferedReader.close();
            }
            if (bufferedWriter != null) {
                bufferedWriter.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
