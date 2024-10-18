package com.franka.chat.data.service;

import com.franka.chat.data.entity.ChatSession;
import com.franka.chat.data.entity.ChatUserKind;
import com.franka.chat.data.entity.ChatUserRole;
import com.franka.chat.data.repository.ChatSessionRepository;
import com.franka.chat.util.CryptUtil;
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

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AuthService {
    public record AuthorizedRoute(String route, String name, Class<? extends Component> view) {}
    private static final String LOGOUT_SUCCESS_URL = "/";

    @Autowired
    private UserDetailsManager userDetailsManager;

    @Autowired
    private AuthenticationContext authenticationContext;

    @Autowired
    private ChatSessionRepository chatSessionRepository;

    public UserDetails getAuthenticatedUser() {
        return authenticationContext.getAuthenticatedUser(UserDetails.class).get();
    }

    public void logout() {
        VaadinSession.getCurrent().getSession().invalidate();
        //authenticationContext.logout();
    }

    private String getNameNotInDb(String name) {
      String searchName = name;
      var dbUser = chatSessionRepository.findByName(name);
      if (dbUser != null) {
        int i = 0;
        boolean isInDb = true;
        while (isInDb) {
          searchName = name + "-" + ++i;
          dbUser = chatSessionRepository.findByName(searchName);
          isInDb = dbUser != null;
        }
      }
      return searchName;
    }

    public Optional<ChatSession> authenticate(String loginUserName, ChatUserKind userKind, String password) throws NoSuchAlgorithmException, InvalidKeySpecException {
        String passwordHash = CryptUtil.getHashString(password);
        String userName;
        List<ChatSession> existingChatSessions = chatSessionRepository.findForAuthentication(loginUserName, userKind);
        ChatSession foundSession = null;
        for (ChatSession session : existingChatSessions) {
            if (CryptUtil.comparePassword(password, session.getPwdHash())) {
                foundSession = session;
                break;
            }
        }
        ChatSession session;
        if (foundSession != null) {
            session = foundSession;
            userName = session.getUserName();
        } else {
            return Optional.ofNullable(null);
            //userName = getNameNotInDb(loginUserName);
            //session = new ChatSession(userName, userKind, passwordHash);
            //chatSessionRepository.save(session);
        }
        configureSecurity(userName);
        return Optional.of(session);
    }

    private void configureSecurity(String userName) {
      if (userDetailsManager.userExists(userName)) {
        userDetailsManager.deleteUser(userName);
      }
      UserDetails userDetails = User.withUsername(userName).password("{noop}").roles(ChatUserRole.USER.name()).build();
      userDetailsManager.createUser(userDetails);

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
