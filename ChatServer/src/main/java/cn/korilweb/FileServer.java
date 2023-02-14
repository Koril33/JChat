package cn.korilweb;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.logging.Logger;

public class FileServer extends Server {

    private final static Logger logger = Logger.getLogger(FileServer.class.getName());

    protected FileServer(int port) {
        super(port);
    }

    @Override
    public void runServer() {
        logger.info("File Server start at port: " + port);


        try (ServerSocket serverSocket = new ServerSocket(port)) {
            Socket socket;
            while (true) {
                logger.info("Wait for client connection...");
                if ((socket = serverSocket.accept()) != null) {
                    logger.info(
                            "connect success..." +
                                    "[ip]: " + socket.getInetAddress() +
                                    ", [port]: " + socket.getPort()
                    );

                    InputStream inputStream = socket.getInputStream();

                    byte[] bytes = inputStream.readAllBytes();
                    logger.info("receive bytes: " + Arrays.toString(bytes));
                    int index = 0;
                    for (byte aByte : bytes) {
                        if (aByte == '\n') {
                            break;
                        }
                        index++;
                    }

                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < index; i++) {
                        sb.append((char) bytes[i]);
                    }
                    String filename = sb.substring("filename:".length(), sb.length());
                    logger.info("file name: " + filename);
                    Path file = Path.of(filename);
                    Files.createFile(file);
                    FileOutputStream fileOutputStream = new FileOutputStream(file.toFile());

                    byte[] data = Arrays.copyOfRange(bytes, index + 1, bytes.length);

                    fileOutputStream.write(data);
                    fileOutputStream.flush();
                    logger.info("file create success...");

                    inputStream.close();
                    fileOutputStream.close();

                    socket.close();
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        int filePort = 18889;
        Server fileServer = new FileServer(filePort);
        fileServer.runServer();
    }
}
