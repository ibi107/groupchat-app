# Real-Time Chat Application

### ./Client.java
Represents a client that connects to a server using a socket. Clients can send and receive messages to/from a group chat.

### ./ClientHandler.java
Represents a handler for individual client connections, each client handler manages a single client.

### ./Server.java
Represents a server that listens for incoming client connections, handling them using individual ClientHandler threads.
