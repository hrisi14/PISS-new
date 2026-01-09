package bg.sofia.uni.fmi.mjt.bookmarksmanager.server;

import bg.sofia.uni.fmi.mjt.bookmarksmanager.command.CommandCreator;
import bg.sofia.uni.fmi.mjt.bookmarksmanager.command.CommandExecutor;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Set;

public class Server {
    public static final int SERVER_PORT = 7777;
    private static final String SERVER_HOST = "localhost";
    private static final int BUFFER_SIZE = 4096;
    private static final CommandExecutor EXECUTOR = new CommandExecutor();

    public static void main(String[] args) {
        try (ServerSocketChannel serverSocketChannel = ServerSocketChannel.open()) {
            serverSocketChannel.bind(new InetSocketAddress(SERVER_HOST, SERVER_PORT));
            serverSocketChannel.configureBlocking(false);
            Selector selector = Selector.open();
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
            ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);
            while (true) {
                int readyChannels = selector.select();
                if (readyChannels == 0) {
                    continue;
                }
                Set<SelectionKey> selectedKeys = selector.selectedKeys();
                Iterator<SelectionKey> keyIterator = selectedKeys.iterator();
                while (keyIterator.hasNext()) {
                    SelectionKey key = keyIterator.next();
                    processReadableKey(key, buffer, selector);
                    keyIterator.remove();
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("There is a problem with the server socket", e);
        }
    }

    static void processReadableKey(SelectionKey key, ByteBuffer buffer, Selector selector) throws IOException {
        if (key.isReadable()) {
            SocketChannel sc = (SocketChannel) key.channel();
            buffer.clear();
            int r = sc.read(buffer);
            if (r < 0) {
                System.out.println("Client has closed the connection");
                sc.close();
                return;
            }
            buffer.flip();
            byte[] clientInputBytes = new byte[buffer.remaining()];
            buffer.get(clientInputBytes);
            String clientInput = new String(clientInputBytes, StandardCharsets.UTF_8);
            String output = EXECUTOR.execute(CommandCreator.newCommand(clientInput), sc);
            System.out.println("Sent to executor");
            buffer.clear();
            buffer.put(output.getBytes());
            buffer.flip();
            sc.write(buffer);
        } else if (key.isAcceptable()) {
            ServerSocketChannel sockChannel = (ServerSocketChannel) key.channel();
            SocketChannel accept = sockChannel.accept();
            accept.configureBlocking(false);
            accept.register(selector, SelectionKey.OP_READ);
        }
    }
}


