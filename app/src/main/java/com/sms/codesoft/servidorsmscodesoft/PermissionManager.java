package com.sms.codesoft.servidorsmscodesoft;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;

/**
 * Created by Carlos on 05/10/2017.
 */

public class PermissionManager {
    //A method that can be called from any Activity, to check for specific permission
    public static void check(Activity activity, String permission, int requestCode){
        //If requested permission isn't Granted yet
        if (ActivityCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
            //Request permission from user
            ActivityCompat.requestPermissions(activity,new String[]{permission},requestCode);
        }
    }
}
