package cn.korilweb;

public class Main {
    public static void main(String[] args) {
        int chatPort = 18888;
        Server chatServer = new ChatServer(chatPort);
        chatServer.runServer();
    }
}