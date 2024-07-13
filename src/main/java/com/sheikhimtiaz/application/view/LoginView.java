package com.sheikhimtiaz.application.view;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@Route("login")
@AnonymousAllowed
public class LoginView extends VerticalLayout {

    public LoginView() {
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);

        var login = new LoginForm();
        login.setAction("login");

        Button signUpButton = new Button("Sign Up", event -> {
            getUI().ifPresent(ui -> ui.navigate("signup"));
        });

        HorizontalLayout buttonsLayout = new HorizontalLayout(login, signUpButton);
        buttonsLayout.setAlignItems(Alignment.CENTER);

        add(
                new H1("Weather app"),
                buttonsLayout
        );
    }
}
