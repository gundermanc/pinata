package com.pinata.iosrvm.views;

import org.robovm.apple.coregraphics.CGRect;
import org.robovm.apple.coregraphics.CGSize;
import org.robovm.apple.uikit.UIButton;
import org.robovm.apple.uikit.UIColor;
import org.robovm.apple.uikit.UIControlState;
import org.robovm.apple.uikit.UIFont;

import com.pinata.iosrvm.Theme;

/**
 * Styled application button. Changing this class changes app look and feel.
 * @author Christian Gunderman
 */
public class Button extends UIButton {

    public Button(CGRect bounds,
                  String title,
                  UIColor normColor,
                  UIColor pressedColor) {
        super(bounds);
        
        this.setBackgroundColor(UIColor.clear());
        this.setTitleColor(normColor, UIControlState.Normal);
        this.setTitleColor(pressedColor, UIControlState.Highlighted);
        this.setTitle(title, UIControlState.Normal);
        this.setFontSize(Theme.UI_TEXT_SIZE);
        this.setTitleShadowColor(Theme.UI_SHADOW_COLOR, UIControlState.Normal);
        this.getTitleLabel().setShadowOffset(new CGSize(2.0f, 2.0f));
    }

    public void setFontSize(float size) {
        this.getTitleLabel().setFont(UIFont.getBoldSystemFont(size));
    }
}
