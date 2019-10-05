package com.officialakbarali.fabiz;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.officialakbarali.fabiz.network.syncInfo.blockPages.AppVersion;
import com.officialakbarali.fabiz.network.syncInfo.blockPages.ForcePull;
import com.officialakbarali.fabiz.network.syncInfo.blockPages.UpdateData;

import static com.officialakbarali.fabiz.data.CommonInformation.getPassword;
import static com.officialakbarali.fabiz.data.CommonInformation.getUsername;
import static com.officialakbarali.fabiz.data.CommonInformation.setPassword;
import static com.officialakbarali.fabiz.data.CommonInformation.setUsername;

public class CommonResumeCheck {
    public CommonResumeCheck(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        boolean appVersionProblem = sharedPreferences.getBoolean("version", false);
        if (appVersionProblem) {
            Intent versionIntent = new Intent(context, AppVersion.class);
            versionIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(versionIntent);
        } else {
            if (getUsername() == null || getPassword() == null) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("my_username", null);
                editor.putString("my_password", null);
                editor.putBoolean("update_data", false);
                editor.putBoolean("force_pull", false);
                editor.apply();

                setUsername(null);
                setPassword(null);

                Intent loginIntent = new Intent(context, LogIn.class);
                loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(loginIntent);
            } else {
                boolean forcePullActivate = sharedPreferences.getBoolean("force_pull", false);
                if (forcePullActivate) {
                    Intent forcePullIntent = new Intent(context, ForcePull.class);
                    forcePullIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(forcePullIntent);
                } else {
                    boolean updateData = sharedPreferences.getBoolean("update_data", false);
                    if (updateData) {
                        Intent updateDataIntent = new Intent(context, UpdateData.class);
                        updateDataIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(updateDataIntent);
                    }
                }
            }
        }
    }
}
