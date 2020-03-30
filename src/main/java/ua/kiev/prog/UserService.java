package ua.kiev.prog;

import ua.kiev.prog.models.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.kiev.prog.repository.*;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final MessageRepository messageRepository;

    public UserService(UserRepository userRepository, MessageRepository messageRepository) {
        this.userRepository = userRepository;
        this.messageRepository = messageRepository;
    }

    @Transactional(readOnly = true)
    public CustomUser findByChatId(long id) {
        return userRepository.findByChatId(id);
    }

    @Transactional(readOnly = true)
    public List<CustomUser> findAllUsers() {
        return userRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<CustomMessage> findUsersMessage() {
        return messageRepository.findAll();
    }

    @Transactional
    public void addUser(CustomUser user) {
        userRepository.save(user);
    }

    @Transactional
    public void addMessage(CustomMessage message) {
        messageRepository.save(message);
    }

    @Transactional
    public void updateUser(CustomUser user) {
        userRepository.save(user);
    }
}

