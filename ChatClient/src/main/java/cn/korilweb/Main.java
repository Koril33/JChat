package cn.korilweb;

public class Main {
    public static void main(String[] args) {
        String host = "localhost";
        int chatPort = 18888;

        Client chatClient = new ChatClient(host, chatPort);
        chatClient.runClient();
    }
}