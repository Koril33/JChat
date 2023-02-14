package cn.korilweb;

import cn.korilweb.entity.User;
import cn.korilweb.task.ChatTask;
import cn.korilweb.task.ServerHeartBeatCheckTask;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class ChatServer extends Server {

    private final static Logger logger = Logger.getLogger(ChatServer.class.getName());

    private final static int MAX_USER_NUM = 4;

    private final static Map<String, ChatTask> userTaskMap = new ConcurrentHashMap<>();

    private final static ExecutorService executorService = Executors.newFixedThreadPool(MAX_USER_NUM);


    private final static Object lock = new Object();


    public ChatServer(int port) {
        super(port);
    }

    @Override
    public void runServer() {

        Thread serverHeartBeatCheckTask = new Thread(new ServerHeartBeatCheckTask(this));
        serverHeartBeatCheckTask.start();

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            Socket socket;
            while (true) {
                logger.info("Wait for client connection...");
                if ((socket = serverSocket.accept()) != null) {
                    logger.info(
                            "Connect success..." +
                                    "[ip]: " + socket.getInetAddress() +
                                    ", [port]: " + socket.getPort() +
                                    ", current client num: " + userTaskMap.size()
                    );
                    ChatTask task = new ChatTask(socket, this);
                    executorService.execute(task);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 将信息广播给当前在线的所有用户
     * @param msg 信息
     */
    public void broadcast(String msg) {
        userTaskMap.values().forEach(chatTask -> chatTask.sendMsg(msg));
    }

    /**
     * 将信息广播给所有人，除了给定的排除用户列表
     * @param msg 信息
     * @param excludeUsernames 排除的用户列表名字
     */
    public void broadcast(String msg, List<String> excludeUsernames) {
        userTaskMap.values().forEach(chatTask -> {
            if (!excludeUsernames.contains(chatTask.getUser().getUsername())) {
                chatTask.sendMsg(msg);
            }
        });
    }


    public void sendTo(String msg, String username) {
        userTaskMap.get(username).sendMsg(msg);
    }

    /**
     * 返回当前在线用户名字列表
     * @return 用户名字列表
     */
    public List<String> getUsernames() {
        return new ArrayList<>(userTaskMap.keySet());
    }


    /**
     * 放回当前在线用户列表
     * @return 用户列表
     */
    public List<User> getUsers() {
        return userTaskMap.values().stream().map(ChatTask::getUser).collect(Collectors.toList());
    }


    public Map<String, ChatTask> getUserTaskMap() {
        return userTaskMap;
    }


    /**
     * 添加新的用户
     * @param username 用户名字
     * @param chatTask 聊天任务线程引用
     */
    public void addUser(String username, ChatTask chatTask) {
        logger.info("New user join chat room: " + username);
        userTaskMap.put(username, chatTask);

        broadcast("新的用户加入聊天室: " + username + ", 当前在线人数: " + userTaskMap.size());
    }


    /**
     * 删除用户
     * @param username 用户名字
     */
    public void removeUser(String username) {
        synchronized (lock) {
            ChatTask task = userTaskMap.get(username);

            try {
                task.getSocket().close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            logger.info("Remove user: " + username);
            userTaskMap.remove(username);
            broadcast("用户退出聊天室: " + username + ", 当前在线人数: " + userTaskMap.size());
        }
    }

}
