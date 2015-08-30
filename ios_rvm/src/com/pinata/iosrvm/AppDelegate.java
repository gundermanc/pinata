package com.pinata.iosrvm;

import java.util.Date;
import org.robovm.apple.foundation.NSAutoreleasePool;
import org.robovm.apple.uikit.UIApplication;
import org.robovm.apple.uikit.UIApplicationDelegateAdapter;
import org.robovm.apple.uikit.UIApplicationLaunchOptions;
import org.robovm.apple.uikit.UIScreen;
import org.robovm.apple.uikit.UIWindow;

import com.pinata.iosrvm.viewcontrollers.GreeterNavigationController;

/**
 * AppDelegate Class. A.k.a.: RoboVM iOS App entry point.
 * @author Christian Gunderman
 */
public class AppDelegate extends UIApplicationDelegateAdapter {
    private UIWindow window;
    private GreeterNavigationController rootController;

    @Override
    public boolean didFinishLaunching (UIApplication application,
                                       UIApplicationLaunchOptions launchOptions) {
        // Set up the view controller.
        this.rootController = new GreeterNavigationController();

        // Create a new window at screen size.
        this.window = new UIWindow(UIScreen.getMainScreen().getBounds());
        // Set our viewcontroller as the root controller for the window.
        this.window.setRootViewController(this.rootController);
        // Make the window visible.
        this.window.makeKeyAndVisible();

        return true;
    }

    public static void main (String[] args) {
        try (NSAutoreleasePool pool = new NSAutoreleasePool()) {
            UIApplication.main(args, null, AppDelegate.class);
        }
    }
}
