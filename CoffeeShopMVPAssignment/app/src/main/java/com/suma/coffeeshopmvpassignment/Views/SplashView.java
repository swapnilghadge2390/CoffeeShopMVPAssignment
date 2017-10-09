package com.suma.coffeeshopmvpassignment.Views;

import android.app.Activity;

/**
 * Created by suma on 05/10/17.
 */

public interface SplashView {
    Activity getViewActivity();
    void onPermissionsGranted();
    void onPermissionsDenied();
    void onPermissionsDeniedWithNeverAskAgain();
    void onPermissionsSettings();
    void openLocationSettingsWindowOkButtonClick();

}
