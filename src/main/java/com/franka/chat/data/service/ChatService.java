package com.franka.chat.data.service;

import com.franka.chat.data.ChatSessionAttribute;
import com.franka.chat.data.entity.Chat;
import com.franka.chat.data.entity.ChatMessage;
import com.franka.chat.data.entity.ChatSession;
import com.franka.chat.data.entity.ChatUserKind;
import com.franka.chat.data.entity.ChatUserRole;
import com.franka.chat.data.repository.ChatMessageRepository;
import com.franka.chat.data.repository.ChatRepository;
import com.franka.chat.data.repository.ChatSessionRepository;
import com.franka.chat.util.CryptUtil;
import com.franka.chat.views.MainLayout;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.spring.security.AuthenticationContext;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class ChatService {
  @Autowired
  private AuthenticationContext authenticationContext;

  @Autowired
  private ChatSessionRepository sessionRepository;
  @Autowired
  private ChatMessageRepository messageRepository;
  @Autowired
  private ChatRepository chatRepository;

  @Autowired
  private HttpServletRequest servletRequest;

  public List<ChatMessage> findChatMessagesByChatId(Long chatId) {
    return messageRepository.findByChatId(chatId);
  }

  public ChatMessage saveChatMessage(ChatMessage msg) {
    return messageRepository.save(msg);
  }

  public List<ChatSession> findAllSessions(String userName) {
    Long currentSessionId = getCurrentSession().getId();
    ChatSession thisSession = sessionRepository.findById(currentSessionId).get();
    List<ChatSession> otherSessions;
    if (userName == null || userName.isEmpty()) {
      otherSessions = sessionRepository.findAllOrdered(currentSessionId);
    } else {
      otherSessions = sessionRepository.findUserNameIgnoreCaseOrdered(currentSessionId, userName);
    }
    return Stream.concat(Stream.of(thisSession), otherSessions.stream())
                                  .collect(Collectors.toList());
  }

  public Optional<ChatSession> findSessionById(Long sessionId) {
    return sessionRepository.findById(sessionId);
  }

  public ChatSession saveSession(ChatSession session) {
    return sessionRepository.save(session);
  }

  @PostConstruct
  public void generateData() throws NoSuchAlgorithmException, InvalidKeySpecException {
    if (VaadinSession.getCurrent() != null) {
      VaadinSession.getCurrent().getSession().invalidate();
      return;
    }

    boolean dataExists = sessionRepository.count() > 0;
    if (dataExists) {
      return;
    }

    deleteAllData();

    String passwordHash = CryptUtil.getHashString("start123");
    ChatSession frank = new ChatSession("Frank", ChatUserKind.MALE, passwordHash);
    frank.setUserRole(ChatUserRole.ADMIN);
    ChatSession klaudia = new ChatSession("Klaudia", ChatUserKind.FEMALE, passwordHash);
    ChatSession jan = new ChatSession("Jan", ChatUserKind.MALE, passwordHash);
    ChatSession svenja = new ChatSession("Svenja", ChatUserKind.FEMALE, passwordHash);
    ChatSession nick = new ChatSession("Nick", ChatUserKind.UNKNOWN, passwordHash);

    List<ChatSession> sessions = Stream.of(frank, klaudia, jan, svenja, nick).collect(Collectors.toList());
    sessionRepository.saveAll(sessions);

    List<ChatSession> moreSessions = new ArrayList<>();
    List<Chat> chats = new ArrayList<>();
    List<ChatMessage> messages = new ArrayList<>();
    for (int i=0; i < 100; i++) {
      ChatSession man = new ChatSession("Man " + i, ChatUserKind.MALE, passwordHash);
      moreSessions.add(man);
      ChatSession woman = new ChatSession("Woman " + i, ChatUserKind.FEMALE, passwordHash);
      moreSessions.add(woman);
      Chat chat = new Chat(man, woman);
      chats.add(chat);
      for (int j = 0; j < 10; j++) {
        ChatSession fromSession;
        ChatSession toSession;
        if (j % 2 == 0) {
          fromSession = man;
          toSession = woman;
        } else {
          fromSession = woman;
          toSession = man;
        }
        ChatMessage msg = new ChatMessage(chat, fromSession,
            String.format("%s to %s: Bla blub %d!", fromSession.getUserName(), toSession.getUserName(), j));
        messages.add(msg);
      }
    }
    sessionRepository.saveAll(moreSessions);
    chatRepository.saveAll(chats);
    messageRepository.saveAll(messages);
  }

  public void deleteAllData() {
    if (messageRepository.count() > 0) {
      messageRepository.deleteAll();
    }
    if (chatRepository.count() == 0) {
      chatRepository.deleteAll();
    }
    if (sessionRepository.count() > 0) {
      sessionRepository.deleteAll();
    }
  }

  public ChatSession getCurrentSession() {
    return (ChatSession)VaadinSession.getCurrent().getAttribute(ChatSessionAttribute.CURRENT_CHAT_SESSION.name());
  }

  public MainLayout getMainLayout() {
    return (MainLayout)VaadinSession.getCurrent().getAttribute(ChatSessionAttribute.MAIN_LAYOUT.name());
  }

  public void setMainLayout(MainLayout mainLayout) {
    VaadinSession.getCurrent().setAttribute(ChatSessionAttribute.MAIN_LAYOUT.name(), mainLayout);
  }

  public List<Chat> findAllChats() {
    return chatRepository.findAll();
  }

  public Optional<Chat> findChatById(Long chatId) {
    return chatRepository.findById(chatId);
  }

  public List<Chat> findChatBySessionIds(Long firstSessionId, Long secondSessionId) {
    return chatRepository.findChatBySessionIds(firstSessionId, secondSessionId);
  }

  public List<Chat> findChatsBySessionId(Long sessionId) {
    return chatRepository.findChatsBySessionId(sessionId);
  }

  public List<Chat> findHiddenChats() {
    return chatRepository.findHiddenChats(getCurrentSession().getId());
  }

  public Chat saveChat(Chat chat) {
    return chatRepository.save(chat);
  }

  public void deleteChat(Chat chat) {
    chatRepository.delete(chat);
  }


    /*
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
    */
}
