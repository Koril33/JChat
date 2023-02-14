package cn.korilweb;

public abstract class Client {

    protected final String host;
    protected final int port;

    protected Client(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public abstract void runClient();
}
