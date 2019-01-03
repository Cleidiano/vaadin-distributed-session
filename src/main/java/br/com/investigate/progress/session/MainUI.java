package br.com.investigate.progress.session;

import com.vaadin.annotations.Push;
import com.vaadin.annotations.Theme;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.ui.Transport;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

@Push(transport = Transport.WEBSOCKET_XHR)
@SpringUI
@Theme(ValoTheme.THEME_NAME)
public class MainUI extends UI {


    @Override
    protected void init(VaadinRequest request) {
        VerticalLayout layout = new VerticalLayout();
        ProgressBarExample.into(layout);
        setContent(layout);
    }
}
