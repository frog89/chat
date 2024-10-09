package com.franka.chat.data.model;

import com.franka.chat.data.entity.Chat;
import com.franka.chat.data.entity.ChatSession;
import com.franka.chat.data.entity.ChatUserKind;
import com.franka.chat.views.ChatView;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.SvgIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.router.RouteParameters;
import org.vaadin.lineawesome.LineAwesomeIcon;

import java.util.function.Function;


public class ChatSideNavItem extends SideNavItem {
  private SvgIcon speakIcon;
  public ChatSideNavItem(ChatSession currentSession, Chat chat, boolean speakIconVisible, Function<Chat,Void> deleteChat) {
    super("");
    ChatSession otherSession = chat.getOtherSession(currentSession);
    RouteParameters parameters = new RouteParameters("chatId", chat.getId().toString());
    SvgIcon closeIcon = LineAwesomeIcon.WINDOW_CLOSE.create();
    closeIcon.setColor("red");
    Button closeButton = new Button(closeIcon, event -> {
      deleteChat.apply(chat);
    });
    closeButton.setTooltipText("Chat verstecken");

    HorizontalLayout hLayout = new HorizontalLayout();
    hLayout.setVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
    this.speakIcon = LineAwesomeIcon.SPEAKAP.create();
    this.speakIcon.setVisible(speakIconVisible);
    hLayout.add(closeButton, this.speakIcon);
    this.setLabel(otherSession.getUserName());
    this.setPath(ChatView.class, parameters);
    this.setPrefixComponent(hLayout);
    this.setId(chat.getId().toString());
    this.addClassName(ChatUserKind.getClassname(otherSession.getUserKind()));
  }

  public void setSpeakIconVisibility(boolean visible) {
    this.speakIcon.setVisible(visible);
  }
}
