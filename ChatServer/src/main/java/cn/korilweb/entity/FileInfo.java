package cn.korilweb.entity;

import java.time.Instant;

public class FileInfo {

    /**
     * 文件的 id
     */
    private String fileId;

    /**
     * 该文件的发送者
     */
    private String senderName;

    /**
     * 文件大小
     */
    private long fileSize;

    /**
     * 文件格式
     */
    private String format;

    /**
     * 发送时间
     */
    private Instant sendTime;

    /**
     * 接收时间
     */
    private Instant receiveTime;

    public FileInfo(String fileId, String senderName, long fileSize, String format) {
        this.fileId = fileId;
        this.senderName = senderName;
        this.fileSize = fileSize;
        this.format = format;
        this.sendTime = Instant.now();
    }


    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public Instant getSendTime() {
        return sendTime;
    }

    public void setSendTime(Instant sendTime) {
        this.sendTime = sendTime;
    }

    public Instant getReceiveTime() {
        return receiveTime;
    }

    public void setReceiveTime(Instant receiveTime) {
        this.receiveTime = receiveTime;
    }
}
