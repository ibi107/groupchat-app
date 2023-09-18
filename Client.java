import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Scanner;

/**
 * The Client class represents a client for a chat application that connects to
 * a server using a Socket.
 * Clients can send and receive messages to/from a group chat.
 */
public class Client {
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String username;

    /**
     * Constructs a new Client object.
     * 
     * @param socket   The Socket object representing the connection to the server.
     * @param username The username of the client.
     */
    public Client(Socket socket, String username) {
        try {
            this.socket = socket;
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.username = username;
        } catch (IOException e) {
            closeAll(socket, bufferedReader, bufferedWriter);
        }
    }

    /**
     * The main method of the Client class.
     * 
     * @param args Command-line arguments (not used).
     * @throws IOException If error occurs opening socket.
     */
    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter your username: ");
        String username = scanner.nextLine();

        Socket socket = new Socket("127.0.0.1", 5000);
        Client client = new Client(socket, username);
        client.listenForMessages();
        client.sendMessage();

        scanner.close();
    }

    /**
     * Sends messages entered by the user to the server.
     */
    public void sendMessage() {
        try {
            bufferedWriter.write(username);
            bufferedWriter.newLine();
            bufferedWriter.flush();

            Scanner scanner = new Scanner(System.in);
            while (socket.isConnected()) {
                String messageToSend = scanner.nextLine();
                bufferedWriter.write(username = ": " + messageToSend);
                bufferedWriter.newLine();
                bufferedWriter.flush();
            }

            scanner.close();
        } catch (IOException e) {
            closeAll(socket, bufferedReader, bufferedWriter);
        }
    }

    /**
     * Listens for messages from the group chat and displays them to the console.
     */
    public void listenForMessages() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String messageFromGroupChat;
                while (socket.isConnected()) {
                    try {
                        messageFromGroupChat = bufferedReader.readLine();
                        System.out.println(messageFromGroupChat);
                    } catch (IOException e) {
                        closeAll(socket, bufferedReader, bufferedWriter);
                    }
                }
            }
        }).start();
    }

    /**
     * Closes the socket, BufferedReader, and BufferedWriter if they are not null.
     * 
     * @param socket         The socket to close.
     * @param bufferedReader The BufferedReader to close.
     * @param bufferedWriter The BufferedWriter to close.
     */
    public void closeAll(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
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
