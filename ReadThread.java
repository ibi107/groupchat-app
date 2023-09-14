import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

/**
 * The class responsible for continuously reading incoming messages from the
 * multicast group.
 */
public class ReadThread implements Runnable {
    private MulticastSocket socket;
    private InetAddress group;
    private int port;
    private static final int MAX_LEN = 1000;

    ReadThread(MulticastSocket socket, InetAddress group, int port) {
        this.socket = socket;
        this.group = group;
        this.port = port;
    }

    /**
     * Runs until finished - recieves datagrams in buffer and converts them into a
     * string.
     */
    @Override
    public void run() {
        while (!GroupChat.finished) {
            byte[] buffer = new byte[ReadThread.MAX_LEN];
            DatagramPacket datagram = new DatagramPacket(buffer, buffer.length, group, port);
            String message;

            try {
                socket.receive(datagram);
                message = new String(buffer, 0, datagram.getLength(), "UTF-8");

                if (!message.startsWith(GroupChat.name))
                    System.out.println(message);
            } catch (IOException e) {
                System.out.println("Socket closed!");
            }
        }
    }
}
