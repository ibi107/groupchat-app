import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.util.Scanner;

/**
 * Main class that controls the group chat application.
 */
public class GroupChat {
    private static final String TERMINATE = "Exit";
    static String name;
    static volatile boolean finished = false;

    /**
     * Joins the multicast group specified host IP and port number, and starts a new
     * ReadThread in a seperate thread to continuously receive messages from the
     * multicast group.
     * 
     * @param args The multicast host IP and the port number specified by the user.
     */
    public static void main(String[] args) {
        if (args.length != 2)
            System.out.println("Two arguments required: <multicast-host> <port-number>");
        else {
            try {
                InetAddress group = InetAddress.getByName(args[0]);
                int port = Integer.parseInt(args[1]);

                Scanner sc = new Scanner(System.in);
                System.out.println("Enter your name: ");
                name = sc.nextLine();

                MulticastSocket socket = new MulticastSocket(port);
                socket.setTimeToLive(0); // TTL set to 0 for local subnet communication
                socket.joinGroup(group);

                Thread t = new Thread(new ReadThread(socket, group, port));
                t.start();

                System.out.println("Start typing messages... \n");
                while (true) {
                    String message;
                    message = sc.nextLine();

                    if (message.equalsIgnoreCase(GroupChat.TERMINATE)) {
                        finished = true;
                        socket.leaveGroup(group);
                        socket.close();
                        break;
                    }

                    message = name + ": " + message;
                    byte[] buffer = message.getBytes();
                    DatagramPacket datagram = new DatagramPacket(buffer, buffer.length, group, port);
                    socket.send(datagram);
                }
            } catch (SocketException se) {
                System.out.println("Error creating sockets");
                se.printStackTrace();

            } catch (IOException ie) {
                System.out.println("Error reading/writing from/to socket");
                ie.printStackTrace();
            }
        }
    }
}