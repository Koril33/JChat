package cn.korilweb.task;

import cn.korilweb.ChatServer;
import cn.korilweb.entity.FileInfo;
import cn.korilweb.entity.User;
import cn.korilweb.util.TimeUtil;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class ChatTask implements Runnable {

    private final static Logger logger = Logger.getLogger(ChatTask.class.getName());

    private Socket socket;

    private ChatServer chatServer;

    private User user;

    private BufferedReader reader;

    private PrintWriter writer;

    public ChatTask(Socket socket, ChatServer chatServer) {
        this.socket = socket;
        this.chatServer = chatServer;
    }

    @Override
    public void run() {
        logger.info("Chat task start run in thread: " + Thread.currentThread().getName());
        try (
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
                PrintWriter writer = new PrintWriter(socket.getOutputStream(), true, StandardCharsets.UTF_8);
        ) {
            this.reader = reader;
            this.writer = writer;

            // 忽略第一个心跳信息
            String firstHeartBeat = reader.readLine();

            String username = reader.readLine();
            while (username == null ||
                    chatServer.getUsernames().contains(username)) {
                sendMsg("该用户名已存在，请换个昵称: ");
                username = reader.readLine();
            }

            user = new User();
            user.setUsername(username);
            user.setLoginTime(Instant.now());
            user.setLastHeartBeatTime(Instant.now());
            chatServer.addUser(username, this);

            String msg;
            while ((msg = reader.readLine()) != null) {
                // 处理指令信息
                if (msg.startsWith("/")) {
                    logger.info("Command from [" + username + "]: " + msg);
                    processCommand(msg);
                    logger.info("Command handle over");
                }
                // 处理普通信息
                else {
                    logger.info("Msg from [" + username + "]: " + msg);
                    // 广播给所有人
                    chatServer.broadcast(username + ": " + msg, List.of(username));
                }
            }

        } catch (IOException e) {
            logger.warning("连接断开...");
        }

    }

    public void sendMsg(String msg) {
        writer.println(msg);
    }

    public User getUser() {
        return user;
    }

    public Socket getSocket() {
        return socket;
    }


    private void processCommand(String msg) {
        if (msg.startsWith("/send")) {
            String targetUsername = msg.split(" ")[1];
            if (!chatServer.getUsernames().contains(targetUsername)) {
                sendMsg("该用户不存在: " + targetUsername);
                return;
            }

            String sendStr = msg.substring("/send ".length() + targetUsername.length() + 1);

            chatServer.sendTo(
                    "私聊消息 " +
                            user.getUsername() +
                            ": " +
                            sendStr,
                    targetUsername
            );
            sendMsg("私聊信息成功发送给: " + targetUsername);
            return;
        }

        if (msg.startsWith("/file")) {
            String targetUsername = msg.split(" ")[1];
            if (!chatServer.getUsernames().contains(targetUsername)) {
                sendMsg("该用户不存在: " + targetUsername);
                return;
            }
            String fileInfoStr = msg.substring("/file ".length() + targetUsername.length() + 1);

            logger.info("接收文件: " + fileInfoStr);
            User targetUser = chatServer.getUsers().stream().filter(u -> u.getUsername().equals(targetUsername)).findFirst().get();
            targetUser.getFiles().add(new FileInfo(UUID.randomUUID().toString(), user.getUsername(), 0L, "test"));

            return;
        }

        if (msg.startsWith("/receive")) {
            String fileId = msg.split(" ")[1];
            if (!user.getFiles().stream().map(FileInfo::getFileId).collect(Collectors.toSet()).contains(fileId)) {
                sendMsg("该文件 id 不存在: " + fileId);
                return;
            }
            String format = "";
            for (FileInfo fileInfo : user.getFiles()) {
                if (fileId.equals(fileInfo.getFileId())) {
                    format = fileInfo.getFormat();
                }
            }
//            String filename = fileId + "." + format;
            String filename = "1.txt";
            Path path = Path.of(filename);

            OutputStream outputStream;
            try (FileInputStream fileInputStream = new FileInputStream(path.toFile())) {
                outputStream = this.socket.getOutputStream();
                outputStream.write(("filename:" + filename + "\n").getBytes());
                outputStream.write(fileInputStream.readAllBytes());
                outputStream.write("\nEOF\n".getBytes());
                outputStream.flush();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }

        switch (msg) {
            case "/users":
                sendMsg(usernames(chatServer.getUsernames()));
                break;
            case "/exit":
                chatServer.removeUser(user.getUsername());
                break;
            case "/time":
                sendMsg("系统当前时间: " + TimeUtil.nowStr());
                break;
            case "/name": sendMsg("你的名字: " + user.getUsername());
                break;
            case "/heartbeat":
                user.setLastHeartBeatTime(Instant.now());
                break;
            case "/info":
                sendMsg("* 用户名: " + user.getUsername() +
                        "\n* 登陆时间: " + TimeUtil.instantToStr(user.getLoginTime()) +
                        "\n* 上次心跳时间: " + TimeUtil.instantToStr(user.getLastHeartBeatTime())
                );
                break;
            case "/folder":
                sendMsg(folder(user));
                break;
            default:
                sendMsg("Unknown command: " + msg);
        }
    }

    private String usernames(List<String> usernames) {
        StringBuilder sb = new StringBuilder();

        for (String name : usernames) {
            sb.append("* ").append(name);
            if (name.equals(user.getUsername())) {
                sb.append(" (you)");
            }
            sb.append("\n");
        }
        sb.append("总人数: ").append(usernames.size());
        return sb.toString();
    }


    private String folder(User user) {
        List<FileInfo> files = user.getFiles();

        if (files.size() == 0) {
            return "还没有人给您发送文件";
        }

        files.sort(Comparator.comparing(FileInfo::getSendTime));

        StringBuilder sb = new StringBuilder();
        files.forEach((FileInfo fileInfo) -> {
            sb.append("* time: ");
            sb.append(TimeUtil.instantToStr(fileInfo.getSendTime()));
            sb.append(", sender: ");
            sb.append(fileInfo.getSenderName());
            sb.append(", fileId: ");
            sb.append(fileInfo.getFileId());
            sb.append(", fileSize: ");
            sb.append(fileInfo.getFileSize());
            sb.append(", fileFormat: ");
            sb.append(fileInfo.getFormat());
            sb.append("\n");
        });
        return sb.toString();
    }
}
