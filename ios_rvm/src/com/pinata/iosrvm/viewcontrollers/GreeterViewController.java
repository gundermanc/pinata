package com.pinata.iosrvm.viewcontrollers;

import org.robovm.apple.coregraphics.CGRect;
import org.robovm.apple.coregraphics.CGSize;
import org.robovm.apple.foundation.NSSet;
import org.robovm.apple.uikit.NSTextAlignment;
import org.robovm.apple.uikit.UIButton;
import org.robovm.apple.uikit.UIColor;
import org.robovm.apple.uikit.UIControlState;
import org.robovm.apple.uikit.UIEvent;
import org.robovm.apple.uikit.UIFont;
import org.robovm.apple.uikit.UIImage;
import org.robovm.apple.uikit.UIImageView;
import org.robovm.apple.uikit.UIKeyboardType;
import org.robovm.apple.uikit.UILabel;
import org.robovm.apple.coregraphics.CGRect;
import org.robovm.apple.coregraphics.CGRectEdge;
import org.robovm.apple.uikit.UIReturnKeyType;
import org.robovm.apple.uikit.UIScreen;
import org.robovm.apple.uikit.UITextBorderStyle;
import org.robovm.apple.uikit.UITextField;
import org.robovm.apple.uikit.UITextFieldDelegateAdapter;
import org.robovm.apple.uikit.UITextFieldViewMode;
import org.robovm.apple.uikit.UITouch;
import org.robovm.apple.uikit.UIView;
import org.robovm.apple.uikit.UIViewController;

import com.pinata.iosrvm.Theme;
import com.pinata.iosrvm.views.Button;

/**
 * GreeterView view controller. Initializes and loads the greeter view window.
 * @author Christian Gunderman
 */
public class GreeterViewController extends UIViewController {
    private UIButton loginButton;
    private UIButton createUserButton;

    public GreeterViewController () {
        UIView view = this.getView();

        view.setBackgroundColor(UIColor.white());

        // Calculate button rects.
        CGRect windowBounds = view.getBounds();
        CGRect loginButtonBounds = new CGRect();
        CGRect createUserButtonBounds = new CGRect();
        windowBounds.divide(loginButtonBounds, createUserButtonBounds,
                            windowBounds.getMidY(), CGRectEdge.MinY);

        // Create buttons.
        this.loginButton = new Button(loginButtonBounds,
                                      "log in",
                                      Theme.UI_COLOR_A,
                                      Theme.UI_COLOR_A_DARK);
        this.loginButton.setBackgroundColor(Theme.UI_COLOR_B);
        this.loginButton.setFontSize(Theme.UI_TEXT_BIG_SIZE);

        this.createUserButton = new Button(createUserButtonBounds,
                                           "sign in",
                                           Theme.UI_COLOR_B,
                                           Theme.UI_COLOR_B_DARK);
        this.createUserButton.setBackgroundColor(Theme.UI_COLOR_A);
        this.createUserButton.setFontSize(Theme.UI_TEXT_BIG_SIZE);

        // Add views to parent.
        view.addSubview(this.loginButton);
        view.addSubview(this.createUserButton);
    }

    @Override
    public void touchesBegan (NSSet<UITouch> touches, UIEvent event) {

    }
}
