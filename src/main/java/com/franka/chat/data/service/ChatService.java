package com.franka.chat.data.service;

import com.franka.chat.data.entity.ChatMessage;
import com.franka.chat.data.entity.ChatSession;
import com.franka.chat.data.entity.ChatUser;
import com.franka.chat.data.entity.ChatUserKind;
import com.franka.chat.data.repository.ChatMessageRepository;
import com.franka.chat.data.repository.ChatSessionRepository;
import com.franka.chat.data.repository.ChatUserRepository;
import com.vaadin.flow.server.VaadinSession;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class ChatService {
    private ChatUserRepository userRepository;
    private ChatSessionRepository sessionRepository;
    private ChatMessageRepository messageRepository;

    @Autowired
    private HttpServletRequest servletRequest;

    public ChatService(ChatUserRepository userRepository,
                       ChatSessionRepository sessionRepository,
                       ChatMessageRepository messageRepository) {
        this.userRepository = userRepository;
        this.sessionRepository = sessionRepository;
        this.messageRepository = messageRepository;
    }

    public List<ChatUser> findAllUsers(String stringFilter) {
        if (stringFilter == null || stringFilter.isEmpty()) {
            return userRepository.findAll();
        } else {
            return userRepository.search(stringFilter);
        }
    }

    public long userCount() {
        return userRepository.count();
    }

    public List<ChatMessage> findAllChatMessages() {
        return messageRepository.findAll();
    }

    @PostConstruct
    public void generateUsers() {
        ChatUser frank = new ChatUser("Frank", ChatUserKind.MALE);
        ChatUser klaudia = new ChatUser("Klaudia", ChatUserKind.FEMALE);
        ChatUser jan = new ChatUser("Jan", ChatUserKind.MALE);
        ChatUser svenja = new ChatUser("Svenja", ChatUserKind.FEMALE);
        ChatUser nick = new ChatUser("Nick", ChatUserKind.UNKNOWN);

        List<ChatUser> users = Stream.of(frank, klaudia, jan, svenja, nick).collect(Collectors.toList());
        if (userRepository.count() == 0) {
            userRepository.saveAll(users);
        }
    }

    public void generateSessions() {
        List<ChatUser> users = userRepository.findAll();
        String ipAddress = VaadinSession.getCurrent().getBrowser().getAddress();
        if (userRepository.count() == 0) {
            userRepository.saveAll(users);
            for(ChatUser user : users) {
                ChatSession session = new ChatSession(user, ipAddress);
                sessionRepository.save(session);
            }
        }
        List<ChatSession> sessions = sessionRepository.findAll();

        if (messageRepository.count() == 0) {
            messageRepository.saveAll(
                    Stream.of("Frank:Klaudia:Hallo, Schatz!",
                            "Klaudia:Frank:Hallo",
                            "Jan:Klaudia:Was gibt es?",
                            "Svenja:Jan:Muss weg!",
                            "Nick:Svenja:Ich hab dich auf Insta gesehen!").map(name -> {
                        ChatMessage msg = new ChatMessage();
                        String[] chatToken = name.split(":");
                        String chatTokenFromUser = chatToken[0];
                        String chatTokenToUser = chatToken[1];
                        String chatTokenMsg = chatToken[2];

                        ChatUser fromUser = users.stream()
                                .filter(u -> chatTokenFromUser.equals(u.getName()))
                                .findAny()
                                .orElse(null);
                        ChatSession fromSession =sessions.stream()
                                .filter(s -> fromUser.getId().equals(s.getChatUser().getId()))
                                .findAny()
                                .orElse(null);

                        ChatUser toUser = users.stream()
                                .filter(u -> chatTokenToUser.equals(u.getName()))
                                .findAny()
                                .orElse(null);
                        ChatSession toSession = sessions.stream()
                                .filter(s -> toUser.getId().equals(s.getChatUser().getId()))
                                .findAny()
                                .orElse(null);

                        msg.setFromChatUser(fromUser);
                        msg.setFromChatUserSessionId(fromSession.getId());
                        msg.setToChatUser(toUser);
                        msg.setToChatUserSessionId(toSession.getId());
                        msg.setMessage(chatTokenMsg);
                        return msg;
                    }).collect(Collectors.toList())
            );
        }
    }
}
