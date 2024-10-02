package com.franka.chat.views.chat;

import com.franka.chat.data.entity.Chat;
import com.franka.chat.data.entity.ChatMessage;
import com.franka.chat.data.entity.ChatSession;
import com.franka.chat.data.service.ChatService;
import com.franka.chat.util.ChatMessageListBroadcasterUtil;
import com.franka.chat.util.NotificationUtil;
import com.franka.chat.views.MainLayout;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.shared.Registration;
import jakarta.annotation.security.PermitAll;

import java.time.format.DateTimeFormatter;
import java.util.List;

@PageTitle("Chat")
@Route(value = "chat/:chatId", layout = MainLayout.class)
@PermitAll
public class ChatView extends VerticalLayout implements BeforeEnterObserver {
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private Registration broadcasterRegistration;
    private Grid<ChatMessage> grid = new Grid<>(ChatMessage.class);
    private Chat chat;

    private ChatService chatService;

    public ChatView(ChatService chatService) {
        this.chatService = chatService;
        addClassName("chat-view");
        setSizeFull();

        HorizontalLayout headerArea = new HorizontalLayout();
        TextField messageField = new TextField("Message");
        Button sendBtn = new Button("Send", event -> {
            try {
                ChatSession session = chatService.getCurrentSession();
                ChatMessage msg = new ChatMessage(this.chat, session, messageField.getValue());
                chatService.saveChatMessage(msg);
                List<ChatMessage> msgList = loadMessages();
                ChatMessageListBroadcasterUtil.broadcast(msgList);
                messageField.setValue("");
            } catch (Exception ex) {
                NotificationUtil.showClosableError(ex.getMessage());
            }
        });
        sendBtn.addClickShortcut(Key.ENTER);

        messageField.getStyle().set("flex-grow", "1");
        sendBtn.getStyle().set("flex-grow", "0");
        sendBtn.getStyle().set("min-width", "auto");
        headerArea.add(messageField, sendBtn);
        headerArea.setDefaultVerticalComponentAlignment(Alignment.BASELINE);
        headerArea.setSizeFull();

        headerArea.getStyle().set("height", "auto");
        headerArea.getStyle().set("flex-grow", "0");
        grid.getStyle().set("flex-grow", "1");

        configureGrid();
        add(
          headerArea,
          grid
        );
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        String chatIdString = event.getRouteParameters().get("chatId").get();
        this.chat = chatService.findChatById(Long.valueOf(chatIdString)).get();
        List<ChatMessage> msgList = loadMessages();
        addToGrid(msgList);
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        UI ui = attachEvent.getUI();
        broadcasterRegistration = ChatMessageListBroadcasterUtil.register(newChatList -> {
            ui.access(() -> addToGrid(newChatList));
        });
    }

    @Override
    protected void onDetach(DetachEvent detachEvent) {
        broadcasterRegistration.remove();
        broadcasterRegistration = null;
    }

    private List<ChatMessage> loadMessages() {
        return chatService.findChatMessagesByChatId(this.chat.getId());
    }

    private void addToGrid(List<ChatMessage> messageList) {
        grid.setItems(messageList);
    }

    private void configureGrid() {
        grid.addClassName("message-grid");
        grid.addThemeVariants(GridVariant.LUMO_COMPACT);
        grid.setSizeFull();
        grid.setColumns();
        ChatSession currentSession = chatService.getCurrentSession();
        grid.addColumn(msg -> msg.getChat().getOtherSession(currentSession).getUserName()).setHeader("From").setWidth("12%").setResizable(true);
        grid.addColumn(msg -> dateFormatter.format(msg.getDatetime())).setHeader("Date").setWidth("13%").setResizable(true);
        grid.addColumn(ChatMessage::getMessage).setHeader("Message").setWidth("75%").setResizable(true);
    }
}
