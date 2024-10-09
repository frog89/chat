package com.franka.chat.views;

import com.franka.chat.data.entity.Chat;
import com.franka.chat.data.entity.ChatMessage;
import com.franka.chat.data.entity.ChatSession;
import com.franka.chat.data.model.ChatBroadcastInfo;
import com.franka.chat.data.model.ChatSideNavItem;
import com.franka.chat.data.service.ChatService;
import com.franka.chat.util.ChatBroadcasterUtil;
import com.franka.chat.util.NotificationUtil;
import com.vaadin.flow.component.Key;
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
import jakarta.annotation.security.PermitAll;

import java.time.format.DateTimeFormatter;
import java.util.List;

@PageTitle(MainLayout.CHAT_LABEL)
@Route(value = MainLayout.CHAT_ROUTE + "/:chatId", layout = MainLayout.class)
@PermitAll
public class ChatView extends VerticalLayout implements BeforeEnterObserver {
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
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
                ChatSession currentSession = chatService.getCurrentSession();
                ChatMessage msg = new ChatMessage(this.chat, currentSession, messageField.getValue());
                msg = chatService.saveChatMessage(msg);
                this.chat.setLastSeenMessageId(msg.getId());
                chatService.saveChat(this.chat);
                List<ChatMessage> msgList = loadMessages();
                ChatBroadcastInfo info = new ChatBroadcastInfo(currentSession, this.chat, msgList);
                ChatBroadcasterUtil.broadcast(info);
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
        this.grid.getStyle().set("flex-grow", "1");

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

        MainLayout mainLayout = this.chatService.getMainLayout();
        ChatSideNavItem navItem = mainLayout.findSideNavItem(this.chat);
        if (navItem != null) {
            navItem.setSpeakIconVisibility(false);
        }
        addToGrid(msgList);
    }

    public Chat getChat() {
        return chat;
    }

    private List<ChatMessage> loadMessages() {
        return chatService.findChatMessagesByChatId(this.chat.getId());
    }

    public void addToGrid(List<ChatMessage> messageList) {
        this.chat.setLastSeenMessageId(messageList == null || messageList.isEmpty() ? 0L : messageList.getFirst().getId());
        this.grid.setItems(messageList);
    }

    private void configureGrid() {
        this.grid.addClassName("message-grid");
        this.grid.addThemeVariants(GridVariant.LUMO_COMPACT);
        this.grid.setSizeFull();
        this.grid.setColumns();
        this.grid.addColumn(msg -> msg.getFromSession().getUserName()).setHeader("From").setWidth("12%").setResizable(true);
        this.grid.addColumn(msg -> dateFormatter.format(msg.getDatetime())).setHeader("Date").setWidth("13%").setResizable(true);
        this.grid.addColumn(ChatMessage::getMessage).setHeader("Message").setWidth("75%").setResizable(true);
    }
}
