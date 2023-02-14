package cn.korilweb.entity;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class User {


    /**
     * 用户名
     */
    private String username;

    /**
     * 登陆时间
     */
    private Instant loginTime;

    /**
     * 上次的心跳时间
     */
    private Instant lastHeartBeatTime;

    /**
     * 收件箱，即收到的文件列表
     */
    private List<FileInfo> files = new ArrayList<>();


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Instant getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(Instant loginTime) {
        this.loginTime = loginTime;
    }

    public Instant getLastHeartBeatTime() {
        return lastHeartBeatTime;
    }

    public void setLastHeartBeatTime(Instant lastHeartBeatTime) {
        this.lastHeartBeatTime = lastHeartBeatTime;
    }

    public List<FileInfo> getFiles() {
        return files;
    }

    public void setFiles(List<FileInfo> files) {
        this.files = files;
    }
}
