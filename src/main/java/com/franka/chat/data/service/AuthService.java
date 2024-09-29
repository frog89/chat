package com.franka.chat.data.service;

import com.franka.chat.data.entity.ChatSession;
import com.franka.chat.data.entity.ChatUser;
import com.franka.chat.data.entity.ChatUserRole;
import com.franka.chat.data.repository.ChatSessionRepository;
import com.franka.chat.data.repository.ChatUserRepository;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.spring.security.AuthenticationContext;
import com.vaadin.flow.spring.security.VaadinAwareSecurityContextHolderStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AuthService {
    public record AuthorizedRoute(String route, String name, Class<? extends Component> view) {}
    private static final String LOGOUT_SUCCESS_URL = "/";

    @Autowired
    private UserDetailsManager userDetailsManager;

    @Autowired
    private AuthenticationContext authenticationContext;

    @Autowired
    private ChatUserRepository chatUserRepository;

    @Autowired
    private ChatSessionRepository chatSessionRepository;

    public UserDetails getAuthenticatedUser() {
        return authenticationContext.getAuthenticatedUser(UserDetails.class).get();
    }

    public void logout() {
        VaadinSession.getCurrent().getSession().invalidate();
        //authenticationContext.logout();
    }

    public ChatSession authenticate(ChatUser authUser) {
        boolean userIsInDb = false;
        if (authUser.getId() != null) {
          var dbUser = chatUserRepository.findById(authUser.getId());
          userIsInDb = dbUser.isPresent();
        }
        if (!userIsInDb) {
          authUser = chatUserRepository.save(authUser);
        }

        if (userDetailsManager.userExists(authUser.getName())) {
            userDetailsManager.deleteUser(authUser.getName());
        }
        UserDetails userDetails = User.withUsername(authUser.getName()).password("{noop}").roles(ChatUserRole.USER.name()).build();
        userDetailsManager.createUser(userDetails);
        configureSecurity(userDetails);

        String ipAddress = VaadinSession.getCurrent().getBrowser().getAddress();
        List<ChatSession> chatSessionList = chatSessionRepository.findForAuthentication(authUser.getName(), ipAddress);
        ChatSession session;
        if (chatSessionList.size() >= 1) {
            session = chatSessionList.get(0);
        } else {
            session = new ChatSession(authUser, ipAddress);
            chatSessionRepository.save(session);
        }
        return session;
    }

    private void configureSecurity(UserDetails userDetails) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        userDetails.getAuthorities().forEach(a -> {
            authorities.add(new SimpleGrantedAuthority(a.getAuthority()));
        });

        Authentication authentication
          = new UsernamePasswordAuthenticationToken(userDetails, "", authorities);
        VaadinAwareSecurityContextHolderStrategy strategy = new VaadinAwareSecurityContextHolderStrategy();

        SecurityContext context = strategy.createEmptyContext();
        context.setAuthentication(authentication);
        strategy.setContext(context);
        VaadinSession.getCurrent().getSession().setAttribute("SPRING_SECURITY_CONTEXT", context);
    }
}
