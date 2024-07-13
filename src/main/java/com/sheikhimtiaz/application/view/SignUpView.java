package com.sheikhimtiaz.application.view;

import com.sheikhimtiaz.application.service.UserService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import org.springframework.beans.factory.annotation.Autowired;

@Route("signup")
@AnonymousAllowed
public class SignUpView extends VerticalLayout {

    private final UserService userService;

    @Autowired
    public SignUpView(UserService userService) {
        this.userService = userService;

        TextField usernameField = new TextField("Username");
        PasswordField passwordField = new PasswordField("Password");
        PasswordField confirmPasswordField = new PasswordField("Confirm Password");

        Button signUpButton = new Button("Sign Up", event -> {
            String username = usernameField.getValue();
            String password = passwordField.getValue();
            String confirmPassword = confirmPasswordField.getValue();

            if (!password.equals(confirmPassword)) {
                Notification.show("Passwords do not match");
                return;
            }

            try {
                userService.registerNewUser(username, password);
                Notification.show("User registered successfully");
                getUI().ifPresent(ui -> ui.navigate("login"));
            } catch (Exception e) {
                Notification.show("Registration failed: " + e.getMessage());
            }
        });

        add(usernameField, passwordField, confirmPasswordField, signUpButton);
    }
}
