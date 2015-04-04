package com.pinata.iosrvm;

import org.robovm.apple.uikit.UIColor;

public abstract class Theme {
    // Colors.
    public static final UIColor UI_COLOR_A 
        = new UIColor(0.996078431f, 0.0f, 0.24705882352f, 1.0f);
    public static final UIColor UI_COLOR_A_DARK
        = new UIColor(0.82745098039f, 0.0f, 0.20392156862f, 1.0f);

    public static final UIColor UI_COLOR_B
        = new UIColor(0.23921568627f, 0.86274509803f, 1.0f, 1.0f);
    public static final UIColor UI_COLOR_B_DARK
        = new UIColor(0.2f, 0.71764705882f, 0.83137254902, 1.0f);
    
    public static final UIColor UI_SHADOW_COLOR
        = new UIColor(0.0f, 0.0f, 0.0f, 0.83137254902f);

    // Sizes.
    public static final float UI_TEXT_BIG_SIZE = 74f;
    public static final float UI_TEXT_SIZE = 45f;
}
