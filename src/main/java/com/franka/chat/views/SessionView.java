package com.franka.chat.views;

import com.franka.chat.data.entity.Chat;
import com.franka.chat.data.entity.ChatSession;
import com.franka.chat.data.entity.ChatUserKind;
import com.franka.chat.data.model.ChatSideNavItem;
import com.franka.chat.data.service.ChatService;
import com.franka.chat.util.NotificationUtil;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

import java.util.List;

@PageTitle(MainLayout.SESSIONS_LABEL)
@Route(value = MainLayout.SESSIONS_ROUTE, layout = MainLayout.class)
@RolesAllowed("ROLE_USER")
public class SessionView extends VerticalLayout {
    private Grid<ChatSession> grid = new Grid<>(ChatSession.class);
    TextField filterText = new TextField();

    private ChatService chatService;

    public SessionView(ChatService chatService) {
        this.chatService = chatService;
        setSpacing(false);
        setSizeFull();
        setJustifyContentMode(JustifyContentMode.CENTER);
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        getStyle().set("text-align", "center");
        configureGrid();

        add(
                getToolbar(),
                grid
        );

        updateGrid();
    }

    private void updateGrid() {
        grid.setItems(chatService.findAllSessions(filterText.getValue()));
    }

    private Component getToolbar() {
        filterText.setPlaceholder("Filtern...");
        filterText.setClearButtonVisible(true);
        filterText.setValueChangeMode(ValueChangeMode.LAZY);
        filterText.addValueChangeListener(e -> updateGrid());
        return filterText;
    }

    private void configureGrid() {
        grid.addClassName("user-grid");
        grid.addThemeVariants(GridVariant.LUMO_COMPACT);
        grid.setSizeFull();
        grid.setColumns();
        grid.addColumn(new ComponentRenderer<>(session -> {
            ChatSession currentSession = chatService.getCurrentSession();
            if (currentSession.getId().equals(session.getId())) {
                return null;
            }

            Button button = new Button("Chat");
            button.setWidthFull();
            button.addClickListener(event -> {
                MainLayout layout = this.chatService.getMainLayout();
                List<Chat> chats = this.chatService.findChatBySessionIds(currentSession.getId(), session.getId());
                if (!chats.isEmpty()) {
                    Chat existingChat = chats.getFirst();
                    existingChat.setIsHidden(currentSession, false);
                    chatService.saveChat(existingChat);
                    layout.refreshChats();
                    return;
                }

                Chat newChat = new Chat(currentSession, session);
                try {
                    newChat = chatService.saveChat(newChat);
                } catch(Throwable t) {
                    NotificationUtil.showClosableError(t.getMessage());
                }

                layout.refreshChats();
                ChatSideNavItem navItem = layout.findSideNavItem(newChat);
                UI.getCurrent().navigate(navItem.getPath());
            });

            return button;
        })).setHeader("").setWidth("100px").setResizable(false);
        grid.addComponentColumn(session -> {
            Span span = new Span(session.getUserName());
            String className = ChatUserKind.getClassname(session.getUserKind());
            span.addClassNames(className, "chat-message");
            span.setWidthFull();
            return span;
        }).setHeader("Name:").setResizable(true);
        grid.addColumn(ChatSession::getUserKind).setHeader("Nutzer Art:").setResizable(true);
        grid.addColumn(ChatSession::getStartTime).setHeader("Gestartet:").setResizable(true);
    }
}
