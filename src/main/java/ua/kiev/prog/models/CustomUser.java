package ua.kiev.prog.models;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class CustomUser {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    private Long chatId;
    private String name;
    private boolean qestion = false;

    @OneToMany(mappedBy = "customUser", cascade = CascadeType.ALL)
    private List<CustomMessage> customMessages = new ArrayList<>();

    public CustomUser() { }

    public CustomUser(Long chatId) {
        this.chatId = chatId;
    }

    public CustomUser(Long chatId, String name) {
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean getQestion() {
        return qestion;
    }

    public void setQestion(boolean qestion) {
        this.qestion = qestion;
    }

    public List<CustomMessage> getCustomMessages() {
        return customMessages;
    }

    public void setCustomMessages(List<CustomMessage> customMessages) {
        this.customMessages = customMessages;
    }

    @Override
    public String toString() {
        return "CustomUser{" +
                "id=" + id +
                ", chatId=" + chatId +
                ", name='" + name + '\'' +
                ", qestion=" + qestion +
                '}';
    }
}
