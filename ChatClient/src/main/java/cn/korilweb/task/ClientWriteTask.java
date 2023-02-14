package cn.korilweb.task;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.UUID;


public class ClientWriteTask implements Runnable {

    private final PrintWriter writer;
    private final BufferedReader in;


    public ClientWriteTask(PrintWriter writer, BufferedReader in) {
        this.writer = writer;
        this.in = in;
    }

    @Override
    public void run() {
        try {
            System.out.print("请输入您的昵称: ");
            while (true) {
                String userInput = in.readLine();
                if (userInput.equals("/exit")) {
                    writer.println(userInput);
                    break;
                }
                else if (userInput.startsWith("/file")) {

                    String targetUsername = userInput.split(" ")[1];
                    String originFilename = userInput.substring("/file ".length() + targetUsername.length() + 1);
                    System.out.println("发送文件: " + originFilename);
                    writer.println(userInput + " " + originFilename);

                    Thread sendFileTask = new Thread(new ClientSendFileTask("localhost", 18889, originFilename));
                    sendFileTask.start();
                    continue;
                }
                else if (userInput.isBlank() || userInput.isEmpty()) {
                    System.out.println("错误: 内容不能为空，请输入内容后再发送");
                    continue;
                }

                writer.println(userInput);
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("连接断开");
        }
    }
}
