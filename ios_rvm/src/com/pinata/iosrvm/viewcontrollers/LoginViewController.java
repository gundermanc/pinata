package com.pinata.iosrvm.viewcontrollers;

import org.robovm.apple.coregraphics.CGRect;
import org.robovm.apple.coregraphics.CGRectEdge;
import org.robovm.apple.coregraphics.CGSize;
import org.robovm.apple.uikit.UIView;
import org.robovm.apple.uikit.UIViewController;

import com.pinata.iosrvm.Theme;
import com.pinata.iosrvm.views.Button;
import com.pinata.iosrvm.views.TextField;

public class LoginViewController extends UIViewController {
    private Button loginButton;
    private TextField usernameTextField;
    private TextField passwordTextField;

    public LoginViewController() {
        UIView view = this.getView();

        // Set background color.
        view.setBackgroundColor(Theme.UI_COLOR_B);

        // Divide the window into the content rectangle and the button.
        CGRect windowBounds = view.getBounds();
        CGRect contentBounds = new CGRect();
        CGRect loginButtonBounds = new CGRect();
        windowBounds.divide(contentBounds, loginButtonBounds,
                            windowBounds.getHeight() * 0.66f, CGRectEdge.MinY);

        // Divide the content rectangle into the placeholder and remainder.
        CGRect placeholderBounds = new CGRect();
        CGRect remainderBounds = new CGRect();
        contentBounds.divide(placeholderBounds, remainderBounds,
                             contentBounds.getHeight() * 0.3f, CGRectEdge.MinY);

        // Divide the remainder rectangle into the username and password.
        CGRect usernameTextFieldBounds = new CGRect();
        CGRect passwordTextFieldBounds = new CGRect();
        double textFieldHeight = remainderBounds.getHeight() * 0.3f;
        remainderBounds.divide(usernameTextFieldBounds, passwordTextFieldBounds,
                               textFieldHeight, CGRectEdge.MinY);
        passwordTextFieldBounds.divide(passwordTextFieldBounds, remainderBounds,
                                      textFieldHeight, CGRectEdge.MinY);
        
        // Create subviews.
        this.loginButton = new Button(loginButtonBounds,
                                      "log in",
                                      Theme.UI_COLOR_A,
                                      Theme.UI_COLOR_A_DARK);
        this.loginButton.setFontSize(Theme.UI_TEXT_BIG_SIZE);
        this.usernameTextField = new TextField(usernameTextFieldBounds,
                                               "username",
                                               Theme.UI_TEXT_COLOR,
                                               Theme.UI_TEXT_COLOR);
        this.passwordTextField = new TextField(passwordTextFieldBounds,
                                               "password",
                                               Theme.UI_TEXT_COLOR,
                                               Theme.UI_TEXT_COLOR);
        this.passwordTextField.setSecureTextEntry(true);
        
        // Add views to parent.
        view.addSubview(this.usernameTextField);
        view.addSubview(this.passwordTextField);
        view.addSubview(this.loginButton);
    }
}
