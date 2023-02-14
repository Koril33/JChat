package cn.korilweb;

import cn.korilweb.task.ClientHeartBeatTask;
import cn.korilweb.task.ClientReadTask;
import cn.korilweb.task.ClientSendFileTask;
import cn.korilweb.task.ClientWriteTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class ChatClient extends Client {


    public ChatClient(String host, int port) {
        super(host, port);
    }

    @Override
    public void runClient() {
        try (Socket socket = new Socket(host, port)) {
            try (
                    PrintWriter writer = new PrintWriter(socket.getOutputStream(), true, StandardCharsets.UTF_8);
                    BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
                    BufferedReader in = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));
            ) {
                Thread readThread = new Thread(new ClientReadTask(reader));
                Thread writeThread = new Thread(new ClientWriteTask(writer, in));
                Thread heartBeatThread = new Thread(new ClientHeartBeatTask(writer));

                readThread.start();
                writeThread.start();
                heartBeatThread.start();

                writeThread.join();

            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("与服务器的连接断开...");
        }
    }
}
