package com.franka.chat.views.chat;

import com.franka.chat.data.entity.ChatMessage;
import com.franka.chat.data.service.ChatService;
import com.franka.chat.views.MainLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;

import java.time.format.DateTimeFormatter;

@PageTitle("Chat")
@Route(value = "", layout = MainLayout.class)
@PermitAll
public class ChatView extends HorizontalLayout {
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH24:mm:ss");
    private Grid<ChatMessage> grid = new Grid<>(ChatMessage.class);

    private ChatService service;

    public ChatView(ChatService service) {
        this.service = service;

        addClassName("chat-view");
        setSizeFull();

        configureGrid();
        add(
          grid
        );

        updateGrid();
    }

    private void updateGrid() {
        grid.setItems(service.findAllChatMessages());
    }

    private void configureGrid() {
        grid.addClassName("message-grid");
        grid.setSizeFull();
        grid.setColumns();
        grid.addColumn(msg -> msg.getFromChatUser().getName()).setHeader("From").setWidth("10%");
        grid.addColumn(msg -> msg.getFromChatUserSessionId()).setHeader("From Session Id").setWidth("10%");
        grid.addColumn(msg -> msg.getToChatUser().getName()).setHeader("To").setWidth("10%");
        grid.addColumn(msg -> msg.getToChatUserSessionId()).setHeader("To Session Id").setWidth("10%");
        grid.addColumn(msg -> dateFormatter.format(msg.getDatetime())).setHeader("Date").setWidth("20%");
        var messageCol = grid.addColumn(ChatMessage::getMessage).setHeader("Message").setWidth("60%");
        //messageCol.setAutoWidth(true);
    }
}
