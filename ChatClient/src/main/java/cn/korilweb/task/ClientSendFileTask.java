package cn.korilweb.task;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

public class ClientSendFileTask implements Runnable {

    private final String host;
    private final int port;

    private final String filePathStr;

    public ClientSendFileTask(String host, int port, String filePathStr) {
        this.host = host;
        this.port = port;
        this.filePathStr = filePathStr;
    }

    @Override
    public void run() {
        try (Socket socket = new Socket(host, port)) {

            Path path = Path.of(filePathStr);
            OutputStream outputStream = socket.getOutputStream();
            byte[] fileBytes = Files.readAllBytes(path);
            String header = "filename:" + path.getFileName() + "\n";
            byte[] headerBytes = header.getBytes(StandardCharsets.UTF_8);
            byte[] bytes = new byte[header.length() + fileBytes.length];

            System.arraycopy(headerBytes, 0, bytes, 0, headerBytes.length);
            System.arraycopy(fileBytes, 0, bytes, headerBytes.length, fileBytes.length);

            System.out.println(Arrays.toString(bytes));

            outputStream.write(bytes);
            outputStream.flush();
            outputStream.close();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
