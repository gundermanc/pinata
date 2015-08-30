package com.pinata.iosrvm.viewcontrollers;

import org.robovm.apple.uikit.UINavigationController;

public class GreeterNavigationController extends UINavigationController {
    public GreeterNavigationController() {
        super(new GreeterViewController());

        // Hide navigation bar for greeter screen.
        this.setNavigationBarHidden(true);
    }
}
