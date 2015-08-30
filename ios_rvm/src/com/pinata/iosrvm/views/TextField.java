package com.pinata.iosrvm.views;

import org.robovm.apple.coregraphics.CGRect;
import org.robovm.apple.coregraphics.CGSize;
import org.robovm.apple.foundation.NSAttributedString;
import org.robovm.apple.uikit.NSAttributedStringAttribute;
import org.robovm.apple.uikit.NSAttributedStringAttributes;
import org.robovm.apple.uikit.NSTextAlignment;
import org.robovm.apple.uikit.UIButton;
import org.robovm.apple.uikit.UIColor;
import org.robovm.apple.uikit.UIControlState;
import org.robovm.apple.uikit.UIFont;
import org.robovm.apple.uikit.UITextField;

import com.pinata.iosrvm.Theme;

public class TextField extends UITextField {
    public TextField(CGRect bounds,
                     String hint,
                     UIColor hintColor,
                     UIColor textColor) {
        super(bounds);

        this.setBackgroundColor(UIColor.clear());
        this.setTextColor(textColor);
        this.setTextAlignment(NSTextAlignment.Center);

        NSAttributedStringAttributes attrib = new NSAttributedStringAttributes();
        attrib.set(NSAttributedStringAttribute.ForegroundColor, hintColor);

        NSAttributedString placeholder = new NSAttributedString(hint);
        this.setAttributedPlaceholder(placeholder);

        this.setFontSize(Theme.UI_TEXT_SIZE);
        //this.getLayer().setShadowColor(Theme.UI_SHADOW_COLOR.getCGColor());
        //this.getLayer().setShadowOffset(new CGSize(2.0f, 2.0f));
    }

    public void setFontSize(float size) {
        this.setFont(UIFont.getBoldSystemFont(size));
    }
}
