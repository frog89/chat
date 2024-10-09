package com.franka.chat.views;

import com.franka.chat.data.entity.Chat;
import com.franka.chat.data.entity.ChatSession;
import com.franka.chat.data.entity.ChatUserKind;
import com.franka.chat.data.model.ChatSideNavItem;
import com.franka.chat.data.service.ChatService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;
import org.jsoup.internal.StringUtil;

import java.util.Comparator;
import java.util.List;

@PageTitle(MainLayout.HIDDEN_CHATS_LABEL)
@Route(value = MainLayout.HIDDEN_CHATS_ROUTE, layout = MainLayout.class)
@RolesAllowed("ROLE_USER")
@CssImport(themeFor = "vaadin-grid", value = "./themes/chat/session-grid.css")
public class HiddenChatsView extends VerticalLayout {
    private Grid<Chat> grid = new Grid<>(Chat.class);
    TextField filterText = new TextField();

    private ChatService chatService;

    public HiddenChatsView(ChatService chatService) {
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
        ChatSession currentSession = chatService.getCurrentSession();
        List<Chat> chats = chatService.findHiddenChats();
        List<Chat> hiddenChats = chats.stream()
            .filter(chat -> {
                String filter = filterText.getValue();
                if (StringUtil.isBlank(filter)) {
                    return true;
                }
                ChatSession otherSession = chat.getOtherSession(currentSession);
                if (filter.toLowerCase().contains(otherSession.getUserName().toLowerCase())) {
                    return true;
                }
                return false;
            })
            .sorted(Comparator.comparing(c -> c.getOtherSession(currentSession).getUserName()))
            .toList();

        grid.setItems(hiddenChats);
    }

    private Component getToolbar() {
        filterText.setPlaceholder("Filtern...");
        filterText.setClearButtonVisible(true);
        filterText.setValueChangeMode(ValueChangeMode.LAZY);
        filterText.addValueChangeListener(e -> updateGrid());
        return filterText;
    }

    private void configureGrid() {
        ChatSession currentSession = chatService.getCurrentSession();
        grid.addClassName("hidden-user-grid");
        grid.addThemeVariants(GridVariant.LUMO_COMPACT);
        grid.setSizeFull();
        grid.setColumns();
        grid.addColumn(new ComponentRenderer<>(chat -> {
            Button button = new Button("Chat");
            button.setWidthFull();
            button.addClickListener(event -> {
                    MainLayout layout = chatService.getMainLayout();
                    chat.setIsHidden(currentSession, false);
                    chatService.saveChat(chat);
                    layout.refreshChats();
                    ChatSideNavItem navItem = layout.findSideNavItem(chat);
                    UI.getCurrent().navigate(navItem.getPath());
                    return;
            });

            return button;
        })).setHeader("").setResizable(true);
        grid.addColumn(chat -> chat.getOtherSession(currentSession).getUserName()).setHeader("Name:").setResizable(true);
        grid.addColumn(chat -> chat.getOtherSession(currentSession).getUserKind()).setHeader("Nutzer Art:").setResizable(true);
        grid.addColumn(chat -> chat.getOtherSession(currentSession).getStartTime()).setHeader("Gestartet:").setResizable(true);
        grid.setClassNameGenerator(chat -> ChatUserKind.getClassname(chat.getOtherSession(currentSession).getUserKind()));
    }
}
