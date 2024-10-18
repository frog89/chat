package com.franka.chat.views;

import com.franka.chat.data.ChatSessionAttribute;
import com.franka.chat.data.entity.ChatSession;
import com.franka.chat.data.entity.ChatUserKind;
import com.franka.chat.data.service.AuthService;
import com.franka.chat.util.NotificationUtil;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@PageTitle(MainLayout.LOGIN_LABEL)
@Route(MainLayout.LOGIN_ROUTE)
@AnonymousAllowed
public class LoginView extends VerticalLayout {
    private TextField usernameField;
    private PasswordField passwordField;
    private ComboBox<String> userKindField;
    private Button loginBtn;

    @Autowired
    private AuthService authService;

    public LoginView() {
        createUsernameField();
        createUserKindField();
        createPasswordField();
        createLoginButton();

        Div div = new Div();
        div.setClassName("login-panel");
        div.add(
          new H1("Chat Login"),
          this.usernameField,
          this.userKindField,
          this.passwordField,
          this.loginBtn
        );
        add(
          div
        );

        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);
    }

    private void createPasswordField() {
        this.passwordField = new PasswordField("Passwort");
        this.passwordField.setWidthFull();
    }

    private void createUsernameField() {
        this.usernameField = new TextField("Nutzername");
        this.usernameField.setWidthFull();
    }

    private void createUserKindField() {
        userKindField = new ComboBox<>("Nutzer Art");
        userKindField.setWidthFull();
        List<String> kindValues = new ArrayList<>();
        for (var kind : ChatUserKind.values()) {
            kindValues.add(kind.name());
        }
        userKindField.setItems(kindValues);
        userKindField.setValue(ChatUserKind.UNKNOWN.name());
        userKindField.setAllowCustomValue(true);
    }

    private void createLoginButton() {
        this.loginBtn = new Button("Login", event -> {
            try {
                if (usernameField.getValue().isBlank()) {
                    NotificationUtil.showClosableError("Bitte Nutzername eingeben!");
                    return;
                }
                if (passwordField.getValue().isBlank()) {
                    NotificationUtil.showClosableError("Bitte Passwort eingeben!");
                    return;
                }
                String trimmedUsername = this.usernameField.getValue().trim();
                String trimmedPassword = this.passwordField.getValue().trim();
                var conv = new ChatUserKind.Converter();
                Optional<ChatSession> optChatSession = authService.authenticate(
                    trimmedUsername,
                    conv.convertToEntityAttribute(userKindField.getValue()),
                    trimmedPassword
                );

                if (!optChatSession.isPresent()) {
                    NotificationUtil.showClosableError("Nutzer oder Passwort falsch!");
                    return;
                }

                ChatSession chatSession = optChatSession.get();

                if (trimmedPassword.startsWith("start")) {
                    UI.getCurrent().navigate(MainLayout.SET_PWD_ROUTE + "/" + chatSession.getId());
                    return;
                }

                VaadinSession.getCurrent().setAttribute(ChatSessionAttribute.CURRENT_CHAT_SESSION.name(), chatSession);
                UI.getCurrent().navigate(MainLayout.SESSIONS_ROUTE);
            } catch (Throwable ex) {
                NotificationUtil.showClosableError("Login failed: " + ex.getMessage());
            }
        });
        this.loginBtn.addClassName("login-btn");
        this.loginBtn.setWidthFull();
        this.loginBtn.addClickShortcut(Key.ENTER);
        this.loginBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
    }
}


