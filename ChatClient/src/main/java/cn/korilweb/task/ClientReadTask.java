package cn.korilweb.task;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Objects;

public class ClientReadTask implements Runnable {

    private final BufferedReader reader;

    public ClientReadTask(BufferedReader reader) {
        this.reader = reader;
    }

    @Override
    public void run() {
        try {
            String msg;
            while (true) {
                if ((msg = reader.readLine()) != null) {
                    if (msg.startsWith("filename:")) {
//                        Thread receiveFileTask = new Thread(new ClientReceiveFileTask(msg, reader));
//                        receiveFileTask.start();
//                        receiveFileTask.join();
//                        continue;
                        String filename = msg.split(":")[1];
                        Path path = Path.of("./receive/" + filename);
                        System.out.println("开始接收文件: " + filename);

                        try (OutputStream fileOutputStream = Files.newOutputStream(path, StandardOpenOption.CREATE)) {
                            while (!Objects.equals(msg = reader.readLine(), "EOF")) {
                                fileOutputStream.write(msg.getBytes(StandardCharsets.UTF_8));
                                fileOutputStream.flush();
                            }
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    System.out.println(msg);
                }
            }
        } catch (IOException e) {
            System.out.println("与服务器的连接断开...");
        }
    }
}
