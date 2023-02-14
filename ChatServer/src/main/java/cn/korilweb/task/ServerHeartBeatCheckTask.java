package cn.korilweb.task;

import cn.korilweb.ChatServer;
import cn.korilweb.entity.User;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

public class ServerHeartBeatCheckTask implements Runnable {

    private final static Logger logger = Logger.getLogger(ServerHeartBeatCheckTask.class.getName());

    private final ChatServer chatServer;

    public ServerHeartBeatCheckTask(ChatServer chatServer) {
        this.chatServer = chatServer;
    }

    @Override
    public void run() {
        while (true) {
            List<User> users = chatServer.getUsers();
            for (User user : users) {
                if (user.getLastHeartBeatTime().isBefore(Instant.now().minusSeconds(5 * 60))) {
                    logger.info("user: " +
                            user.getUsername() +
                            "'s heartbeat is timeout..."
                    );
                    chatServer.removeUser(user.getUsername());
                }
                else if (chatServer.getUserTaskMap().get(user.getUsername()).getSocket().isClosed()) {
                    logger.info("user: " +
                            user.getUsername() +
                            "'s socket connection is closed..."
                    );
                    chatServer.removeUser(user.getUsername());
                }
            }
        }
    }
}
