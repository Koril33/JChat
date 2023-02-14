package cn.korilweb;

public abstract class Server {

    protected final int port;

    protected Server(int port) {
        this.port = port;
    }

    public abstract void runServer();
}
