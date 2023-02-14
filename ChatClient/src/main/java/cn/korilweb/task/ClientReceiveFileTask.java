package cn.korilweb.task;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Objects;

public class ClientReceiveFileTask implements Runnable {


    private String msg;

    private BufferedReader reader;

    public ClientReceiveFileTask(String msg, BufferedReader reader) {
        this.msg = msg;
        this.reader = reader;
    }

    @Override
    public void run() {
        String filename = msg.split(":")[1];
        Path path = Path.of("./receive/" + filename);
        System.out.println("开始接受文件: " + filename);

        try (OutputStream fileOutputStream = Files.newOutputStream(path, StandardOpenOption.CREATE)) {
            while (!Objects.equals(msg = reader.readLine(), "EOF\n")) {
                fileOutputStream.write(msg.getBytes(StandardCharsets.UTF_8));
                fileOutputStream.flush();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
