package com.franka.chat.views;

import com.franka.chat.data.entity.Chat;
import com.franka.chat.data.entity.ChatSession;
import com.franka.chat.data.model.ChatSideNavItem;
import com.franka.chat.data.service.AuthService;
import com.franka.chat.data.service.ChatService;
import com.franka.chat.util.ChatBroadcasterUtil;
import com.franka.chat.util.NotificationUtil;
import com.franka.chat.views.chat.ChatView;
import com.franka.chat.views.info.InfoView;
import com.franka.chat.views.session.SessionView;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.HasElement;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Footer;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Header;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.shared.Registration;
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
@CssImport(value = "./themes/chat/session-grid.css")
public class MainLayout extends AppLayout implements AfterNavigationObserver {
    public static final String INFO_LABEL_ID = "@Info_ID@";
    public static final String INFO_LABEL = "Info";
    public static final String INFO_ROUTE = "info";

    public static final String SESSIONS_LABEL_ID = "@Sessions_ID@";
    public static final String SESSIONS_LABEL = "Sessions";
    public static final String SESSIONS_ROUTE = "sessions";

    public static final String CHAT_LABEL = "Chat";
    public static final String CHAT_ROUTE = "chat";

    private HasElement activeView;
    private SideNav sideNav;

    private List<Chat> chatList;
    private Registration broadcasterRegistration;

    private ChatService chatService;

    private AuthService authService;

    private H1 viewTitle;

    public MainLayout(ChatService chatService, AuthService authService) {
        this.chatService = chatService; // Cannot be autowired
        chatService.setMainLayout(this);
        this.authService = authService; // Cannot be autowired
        this.sideNav = new SideNav();
        setPrimarySection(Section.DRAWER);
        addDrawerContent();
        addHeaderContent();
    }

    @Override
    public void afterNavigation(AfterNavigationEvent event) {
        this.activeView = event.getActiveChain().get(0);
        System.out.println("Active View: " + this.activeView.toString());
        viewTitle.setText(getCurrentPageTitle());
    }

    private String getCurrentPageTitle() {
        PageTitle pageTitle = getContent().getClass().getAnnotation(PageTitle.class);

        String title = pageTitle == null ? "" : pageTitle.value();

        if (this.activeView.getClass().equals(ChatView.class)) {
            ChatView chatView =  (ChatView)activeView;
            ChatSession currentSession = this.chatService.getCurrentSession();
            ChatSession otherSession = chatView.getChat().getOtherSession(currentSession);
            title = String.format("%s with %s", title, otherSession.getUserName());
        }
        return title;
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        UI ui = attachEvent.getUI();
        broadcasterRegistration = ChatBroadcasterUtil.register(broadcastInfo -> {
            ui.access(() -> {
                ChatSession currentSession = this.chatService.getCurrentSession();
                System.out.println("Broadcast info: " + currentSession.getUserName());

                if (this.activeView.getClass().equals(ChatView.class)) {
                    ChatView chatView =  (ChatView)activeView;
                    if (!chatView.getChat().getId().equals(broadcastInfo.getChat().getId())) {
                        return;
                    }
                    chatView.addToGrid(broadcastInfo.getMessages());
                    return;
                }

                List<Chat> foundChats = this.chatList.stream()
                    .filter(c -> c.getId().equals(broadcastInfo.getChat().getId()))
                    .toList();
                if (foundChats.isEmpty()) {
                    if (broadcastInfo.getChat().hasCurrentSession(currentSession)) {
                        this.chatList.add(broadcastInfo.getChat());
                        SideNavItem navItem = createChatSideNavItem(currentSession, broadcastInfo.getChat(), true);
                        this.sideNav.addItem(navItem);
                    }
                    return;
                }

                if (foundChats.size() > 1) {
                    NotificationUtil.showClosableError("More than one chat found with Id " + broadcastInfo.getChat().getId());
                    return;
                }

                Chat chat = foundChats.getFirst();
                Long biggestBroadcastMessageId =
                    broadcastInfo.getMessages() == null || broadcastInfo.getMessages().isEmpty() ? 0L
                    : broadcastInfo.getMessages().getFirst().getId();
                if (chat.getLastSeenMessageId() < biggestBroadcastMessageId) {
                    ChatSideNavItem itemToMark = findSideNavItem(chat);
                    if (itemToMark != null) {
                        if (broadcastInfo.getFromSession().getId().equals(currentSession.getId())) {
                          chat.setLastSeenMessageId(biggestBroadcastMessageId);
                        } else {
                            itemToMark.setSpeakIconVisibility(true);
                        }
                    }
                }
            });
        });
    }

    @Override
    protected void onDetach(DetachEvent detachEvent) {
        broadcasterRegistration.remove();
        broadcasterRegistration = null;
    }

    public ChatSideNavItem findSideNavItem(Chat chat) {
        List<SideNavItem> foundNavItemList = this.sideNav.getItems().stream().filter(sni -> {
            String sideNavItemIdString = sni.getId().get();
            if (sideNavItemIdString.equals(SESSIONS_LABEL_ID) || sideNavItemIdString.equals(INFO_LABEL_ID)) {
                return false;
            }
            Long sideNavItemId = Long.valueOf(sideNavItemIdString);
            return chat.getId().equals(sideNavItemId);
        }).toList();
        if (foundNavItemList.isEmpty()) {
            return null;
        }
        if (foundNavItemList.size() > 1) {
            NotificationUtil.showClosableError("More than one nav item found for ChatId " + chat.getId());
            return null;
        }
        return (ChatSideNavItem)foundNavItemList.getFirst();
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
        SideNavItem sessionsItem = new SideNavItem(SESSIONS_LABEL, SessionView.class, LineAwesomeIcon.USER_ALT_SOLID.create());
        sessionsItem.setId(SESSIONS_LABEL_ID);
        this.sideNav.addItem(sessionsItem);
        SideNavItem infoItem = new SideNavItem(INFO_LABEL, InfoView.class, LineAwesomeIcon.INFO_CIRCLE_SOLID.create());
        infoItem.setId(INFO_LABEL_ID);
        this.sideNav.addItem(infoItem);
        refreshChats();
    }

    public void refreshChats() {
        List<SideNavItem> chatItemsToRemove = new ArrayList<>();
        this.sideNav.getItems().forEach(ni -> {
            String niId = ni.getId().get();
            if (SESSIONS_LABEL_ID.equals(niId) || INFO_LABEL_ID.equals(niId)) {
                return;
            }
            chatItemsToRemove.add(ni);
        });
        this.sideNav.remove(chatItemsToRemove.toArray(new SideNavItem[]{}));

        ChatSession currentSession = chatService.getCurrentSession();
        this.chatList = chatService.findChatsBySessionId(currentSession.getId());
        for (Chat chat : this.chatList) {
            if (chat.getIsHidden(currentSession)) {
                continue;
            }
            SideNavItem navItem = createChatSideNavItem(currentSession, chat, false);
            this.sideNav.addItem(navItem);
        }
    }

    private ChatSideNavItem createChatSideNavItem(ChatSession currentSession, Chat chat, boolean speakIconVisible) {
        ChatSideNavItem item = new ChatSideNavItem(currentSession, chat, speakIconVisible, chatToDelete -> {
            UI.getCurrent().access(() -> {
                UI.getCurrent().navigate(SESSIONS_ROUTE);
                chatToDelete.setIsHidden(currentSession, true);
                chatService.saveChat(chatToDelete);
                refreshChats();
            });
            return null;
        });
        return item;
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
}
