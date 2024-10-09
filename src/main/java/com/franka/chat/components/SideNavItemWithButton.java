package com.franka.chat.components;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.router.RouteParameters;

public class SideNavItemWithButton extends SideNavItem {

  public SideNavItemWithButton(Icon buttonIcon, String text, Class<? extends Component> clazz, RouteParameters parameters, Component prefixComponent) {
    super(text, clazz, parameters, prefixComponent);
    Button button = new Button(buttonIcon);
    HorizontalLayout layout = new HorizontalLayout();
    layout.add(button, new H2(text));
    getElement().appendChild(layout.getElement());
  }
}
