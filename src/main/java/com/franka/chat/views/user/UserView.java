package com.franka.chat.views.user;

import com.franka.chat.data.entity.ChatUser;
import com.franka.chat.data.service.ChatService;
import com.franka.chat.views.MainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

@PageTitle("Users")
@Route(value = "user", layout = MainLayout.class)
@RolesAllowed("ROLE_USER")
public class UserView extends VerticalLayout {
    private Grid<ChatUser> grid = new Grid<>(ChatUser.class);
    TextField filterText = new TextField();
    private ChatService service;

    public UserView(ChatService service) {
        this.service = service;
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
        grid.setItems(service.findAllUsers(filterText.getValue()));
    }

    private Component getToolbar() {
        filterText.setPlaceholder("Filter users by name...");
        filterText.setClearButtonVisible(true);
        filterText.setValueChangeMode(ValueChangeMode.LAZY);
        filterText.addValueChangeListener(e -> updateGrid());
        return filterText;
    }

    private void configureGrid() {
        grid.addClassName("user-grid");
        grid.setSizeFull();
        grid.setColumns("name");
    }

}
