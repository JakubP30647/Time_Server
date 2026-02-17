/**
 * @author Pawlik Jakub S30647
 */

package zad1;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class Client {
    private String host;
    private int port;
    private String id;

    private SocketChannel socketChannel;

    public Client(String host, int port, String id) {
        this.host = host;
        this.port = port;
        this.id = id;
    }

    public void connect() {
        try {
            socketChannel = SocketChannel.open();
            socketChannel.configureBlocking(false);

            socketChannel.connect(new InetSocketAddress(host, port));
            while (!socketChannel.finishConnect()) {

            }


        } catch (Exception e) {
            System.out.println("ez");

        }
    }

    public String send(String req)  {
        try {
            ByteBuffer writeBuffer = ByteBuffer.wrap(req.getBytes());
            socketChannel.write(writeBuffer);


            ByteBuffer readBuffer = ByteBuffer.allocate(1024);
            int bytesRead = 0;


            StringBuilder response = new StringBuilder();
            while (true) {
                bytesRead = socketChannel.read(readBuffer);

                if (bytesRead > 0) {
                    readBuffer.flip();
                    byte[] data = new byte[readBuffer.limit()];
                    readBuffer.get(data);
                    response.append(new String(data).trim());
                    break;
                }
            }

            return response.toString();
        } catch (IOException e) {
            System.out.println("ez");;
        }

        return "co sie stalo???????????????????????????????????????????????????????????????????????????????";
    }
}


