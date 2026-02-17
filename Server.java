/**
 *
 *  @author Pawlik Jakub S30647
 *
 */

package zad1;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Server {
    String host;
    int port;

    ServerSocketChannel serverSocketChannel;
    Selector selector;
    Thread serverThread;
    volatile boolean running = false;
    StringBuilder log = new StringBuilder();
    Map<String, List<String>> clientLogs = new HashMap<>();
    Map<SocketChannel, String> clientIds = new HashMap<>();

    public Server(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void startServer() {
        running = true;
        serverThread = new Thread(() -> {
            try {
                serverSocketChannel = ServerSocketChannel.open();
                serverSocketChannel.configureBlocking(false);
                serverSocketChannel.bind(new InetSocketAddress(host, port));

                selector = Selector.open();
                serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);



                while (running) {
                    selector.select();

                    Iterator<SelectionKey> keys = selector.selectedKeys().iterator();
                    while (keys.hasNext()) {
                        SelectionKey key = keys.next();
                        keys.remove();

                        if (!key.isValid()) continue;

                        if (key.isAcceptable()) {
                            accept(key);
                        } else if (key.isReadable()) {
                            read(key);
                        }
                    }
                }
            } catch (IOException e) {
                log.append("Server error: ").append(e.getMessage()).append("\n");
            } finally {
                try {
                    if (selector != null) selector.close();
                    if (serverSocketChannel != null) serverSocketChannel.close();
                } catch (IOException e) {
                    log.append("Error closing server: ").append(e.getMessage()).append("\n");
                }
            }
        });
        serverThread.start();
    }

    private void accept(SelectionKey key) {
        try {
            ServerSocketChannel server = (ServerSocketChannel) key.channel();
            SocketChannel client = server.accept();
            client.configureBlocking(false);
            client.register(selector, SelectionKey.OP_READ);
        } catch (IOException e) {
            log.append("Accept error: ").append(e.getMessage()).append("\n");
        }
    }

    private void read(SelectionKey key) {
        SocketChannel client = (SocketChannel) key.channel();
        String id = clientIds.get(client);
        String time = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss.SSS"));

        ByteBuffer buffer = ByteBuffer.allocate(1024);
        try {
            int bytesRead = client.read(buffer);
            if (bytesRead == -1) {
                if (id != null) {
                    log.append(id).append(" disconnected at ").append(time).append("\n");
                }
                client.close();
                key.cancel();
                return;
            }

            buffer.flip();
            String received = new String(buffer.array(), 0, buffer.limit()).trim();

            if (received.startsWith("login")) {
                id = received.split(" ")[1];
                clientIds.put(client, id);
                clientLogs.put(id, new ArrayList<>());
                log.append(id).append(" logged in at ").append(time).append("\n");
                clientLogs.get(id).add("logged in");

                sendResponse(client, "Login OK");

            } else if (received.equals("bye")) {
                if (id != null) {
                    log.append(id).append(" logged out at ").append(time).append("\n");
                    clientLogs.get(id).add("logged out");
                }
                sendResponse(client, "Bye");

            } else if (received.equals("bye and log transfer")) {
                if (id != null) {
                    log.append(id).append(" logged out at ").append(time).append("\n");
                    clientLogs.get(id).add("logged out");

                    StringBuilder clientLog = new StringBuilder();
                    clientLog.append("=== ").append(id).append(" log start ===\n");
                    for (String entry : clientLogs.get(id)) {
                        clientLog.append(entry).append("\n");
                    }
                    clientLog.append("=== ").append(id).append(" log end ===");

                    sendResponse(client, clientLog.toString());
                } else {
                    sendResponse(client, "Not logged in");
                }

            } else {
                if (id != null) {
                    String result = Time.passed(received.split(" ")[0], received.split(" ")[1]);
                    log.append(id).append(" request at ").append(time).append(": \"").append(received).append("\"\n");
                    clientLogs.get(id).add("Request: " + received + "\nResult:\n" + result);
                }
                sendResponse(client, "OK: " + received);
            }

        } catch (IOException e) {
            log.append("Read error: ").append(e.getMessage()).append("\n");
            try {
                client.close();
                key.cancel();
            } catch (IOException ex) {
                log.append("Error closing client: ").append(ex.getMessage()).append("\n");
            }
        }
    }

    private void sendResponse(SocketChannel client, String message) {
        try {
            ByteBuffer buffer = ByteBuffer.wrap(message.getBytes());
            client.write(buffer);
        } catch (IOException e) {
            log.append("Send error: ").append(e.getMessage()).append("\n");
        }
    }

    public void stopServer() {
        running = false;
        try {
            if (selector != null) selector.wakeup();
            if (serverThread != null) serverThread.join();

        } catch (InterruptedException e) {
            log.append("Error stopping server: ").append(e.getMessage()).append("\n");
        }
    }

    public String getServerLog() {
        return log.toString();
    }
}
