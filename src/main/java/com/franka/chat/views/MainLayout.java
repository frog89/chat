package com.franka.chat.views;

import com.franka.chat.data.entity.Chat;
import com.franka.chat.data.entity.ChatSession;
import com.franka.chat.data.entity.ChatUserKind;
import com.franka.chat.data.service.AuthService;
import com.franka.chat.data.service.ChatService;
import com.franka.chat.views.chat.ChatView;
import com.franka.chat.views.info.InfoView;
import com.franka.chat.views.session.SessionView;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Footer;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Header;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.SvgIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.RouteParameters;
import com.vaadin.flow.theme.lumo.LumoUtility;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.vaadin.lineawesome.LineAwesomeIcon;

import java.util.ArrayList;
import java.util.List;

/**
 * The main view is a top-level placeholder for other views.
 */
//@PreserveOnRefresh
@CssImport(value = "./themes/chat/session-user-colors.css")
public class MainLayout extends AppLayout {
    private final String INFO_LABEL = "Info";
    private final String SESSIONS_LABEL = "Sessions";

    SideNav sideNav;

    private ChatService chatService;

    private AuthService authService;

    private H1 viewTitle;

    public MainLayout(ChatService chatService, AuthService authService) {
        this.chatService = chatService; // Cannot be autowired
        this.authService = authService; // Cannot be autowired
        this.sideNav = new SideNav();
        setPrimarySection(Section.DRAWER);
        addDrawerContent();
        addHeaderContent();
    }

    private void addHeaderContent() {
        DrawerToggle toggle = new DrawerToggle();
        toggle.setAriaLabel("Menu toggle");

        viewTitle = new H1();
        viewTitle.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Margin.NONE);

        String u = authService == null ? "": authService.getAuthenticatedUser().getUsername();
        Button logoutBtn = new Button("Log out " + u, e -> authService.logout());
        logoutBtn.getStyle().set("margin-left", "auto");

        var header = new HorizontalLayout(toggle, viewTitle, logoutBtn);
        header.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        header.setWidthFull();
        header.addClassNames(
            LumoUtility.Padding.Vertical.NONE,
            LumoUtility.Padding.Horizontal.MEDIUM);

        addToNavbar(header);
    }

    private void addDrawerContent() {
        Span appName = new Span("Easy Chat");
        appName.addClassNames(LumoUtility.FontWeight.SEMIBOLD, LumoUtility.FontSize.LARGE);
        Header header = new Header(appName);

        fillSideNav();
        Scroller scroller = new Scroller(sideNav);

        addToDrawer(header, scroller, createFooter());
    }

    private void fillSideNav() {
        this.sideNav.addItem(new SideNavItem(SESSIONS_LABEL, SessionView.class, LineAwesomeIcon.USER_ALT_SOLID.create()));
        this.sideNav.addItem(new SideNavItem(INFO_LABEL, InfoView.class, LineAwesomeIcon.INFO_CIRCLE_SOLID.create()));
        refreshChats();
    }

    public void refreshChats() {
        List<SideNavItem> chatItems = new ArrayList<>();
        this.sideNav.getItems().forEach(ni -> {
            if (ni.getLabel().equals(SESSIONS_LABEL) || ni.getLabel().equals(INFO_LABEL)) {
                return;
            }
            chatItems.add(ni);
        });
        this.sideNav.remove(chatItems.toArray(new SideNavItem[]{}));
        ChatSession currentSession = chatService.getCurrentSession();
        for (Chat chat : chatService.findChatsBySessionId(currentSession.getId())) {
            ChatSession otherSession = chat.getOtherSession(currentSession);
            RouteParameters parameters = new RouteParameters("chatId", chat.getId().toString());
            SvgIcon closeIcon = LineAwesomeIcon.WINDOW_CLOSE.create();
            closeIcon.setColor("red");
            Button closeButton = new Button(closeIcon, event -> {
                chatService.deleteChat(chat);
                refreshChats();
            });
            HorizontalLayout hLayout = new HorizontalLayout();
            hLayout.setVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
            hLayout.add(closeButton, LineAwesomeIcon.SPEAKAP.create());
            SideNavItem navItem = new SideNavItem(otherSession.getUserName(), ChatView.class, parameters, hLayout);
            navItem.addClassName(ChatUserKind.getClassname(otherSession.getUserKind()));
            this.sideNav.addItem(navItem);
        }
    }

    private boolean isUserAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            for (GrantedAuthority authority : authentication.getAuthorities()) {
                if ("ROLE_ADMIN".equals(authority.getAuthority())) {
                    return true;
                }
            }
        }
        return false;
    }

    private Footer createFooter() {
        Footer layout = new Footer();

        return layout;
    }

    @Override
    protected void afterNavigation() {
        super.afterNavigation();
        viewTitle.setText(getCurrentPageTitle());
    }

    private String getCurrentPageTitle() {
        PageTitle title = getContent().getClass().getAnnotation(PageTitle.class);
        return title == null ? "" : title.value();
    }
}
