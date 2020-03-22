package ua.kiev.prog;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.ArrayList;
import java.util.List;

@Entity
public class User {
    @Id
    @GeneratedValue
    private Long id;

    private Long chatId;
    private String name;
    private Boolean admin;
    private boolean qestion = false;
    private int countRequest = 0;
    private String lastRequest;

    public String getLastRequest() {
        return lastRequest;
    }

    public void setLastRequest(String lastRequest) {
        this.lastRequest = lastRequest;
    }

    public boolean getQestion() {
        return qestion;
    }

    public int getCountRequest() {
        return countRequest;
    }

    public void setCountRequest(int countRequest) {
        this.countRequest = countRequest;
    }

    public void setQestion(boolean qestion) {
        this.qestion = qestion;
    }

    public User() {
    }

    public User(Long chatId) {
        this.chatId = chatId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public User(Long chatId, Boolean admin) {
        this.chatId = chatId;
        this.admin = admin;
    }

    public User(Long chatId, String name) {
        this.chatId = chatId;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public Boolean getAdmin() {
        return admin;
    }

    public void setAdmin(Boolean admin) {
        this.admin = admin;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", chatId=" + chatId +
                ", name='" + name + '\'' +
                ", admin=" + admin +
                ", qestion=" + qestion +
                ", countRequest=" + countRequest +
                ", lastRequest='" + lastRequest + '\'' +
                '}';
    }
}
