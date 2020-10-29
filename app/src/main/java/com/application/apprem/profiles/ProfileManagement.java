package com.application.apprem.profiles;

import android.content.Context;
import android.content.SharedPreferences;


import com.application.apprem.R;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.PreferenceManager;

public abstract class ProfileManagement {
    private final static char splitChar = '%';
    @NonNull
    private static ArrayList<Profile> profileList = new ArrayList<>();
    private static int preferredProfile;

    public static Profile getProfile(int pos) {
        return profileList.get(pos);
    }

    public static void addProfile(Profile k) {
        profileList.add(k);
    }

    public static void editProfile(int position, Profile newP) {
        profileList.remove(position);
        profileList.add(position, newP);
    }

    public static void removeProfile(int position) {
        profileList.remove(position);
    }

    public static int getSize() {
        return profileList.size();
    }

    @NonNull
    public static ArrayList<Profile> getProfileList() {
        return profileList;
    }

    private static void reload(Context context) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        String pref = sharedPref.getString("profiles", "");
        ArrayList<Profile> pList = new ArrayList<>();
        if (pref.trim().isEmpty()) {
            String name = context.getString(R.string.profile_default_name);
            pList.add(new Profile(name));
        } else {
            String[] profiles = pref.split("" + splitChar);
            for (String s : profiles) {
                try {
                    Profile p = new Profile(s);
                    pList.add(p);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        profileList = pList;
        preferredProfile = sharedPref.getInt("preferred_position", 0);
        resetSelectedProfile(context);
    }

    public static boolean isUninit() {
        return getProfileList() == null || getProfileList().size() == 0;
    }

    public static void initProfiles(Context context) {
        if (isUninit())
            reload(context);
    }

    public static void save(Context context, boolean apply) {
        StringBuilder all = new StringBuilder();
        for (Profile p : profileList) {
            all.append(p.toString()).append(splitChar);
        }

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("profiles", all.toString());
        editor.putInt("preferred_position", preferredProfile);
        if (apply)
            editor.apply();
        else
            editor.commit();
    }

    public static boolean isMoreThanOneProfile() {
        return getSize() > 1;
    }

    @NonNull
    public static ArrayList<String> getProfileListNames() {
        ArrayList<String> a = new ArrayList<>();
        for (Profile p : profileList) {
            a.add(p.getName());
        }
        return a;
    }

    //Positions
    public static void setSelectedProfile(Context context, int position) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt("selected", position);
        editor.apply();
    }

    public static void resetSelectedProfile(Context context) {
        setSelectedProfile(context, loadPreferredProfilePosition());
    }

    public static Profile getSelectedProfile(Context context) {
        return getProfile(getSelectedProfilePosition(context));
    }

    public static int getSelectedProfilePosition(Context context) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPref.getInt("selected", 0);
    }

    //Preferred Profile
    public static void checkPreferredProfile() {
        if (preferredProfile >= getSize()) {
            setPreferredProfilePosition(0);
        }
    }

    public static int getPreferredProfilePosition() {
        return preferredProfile;
    }

    public static void setPreferredProfilePosition(int value) {
        if (value == preferredProfile)
            preferredProfile = -1;
        else
            preferredProfile = value;
    }

    public static boolean isPreferredProfile() {
        return (preferredProfile < getSize() && preferredProfile >= 0) || !isMoreThanOneProfile();
    }

    public static int loadPreferredProfilePosition() {
        if (preferredProfile < 0 || preferredProfile >= getSize())
            return 0;
        return preferredProfile;
    }

    @Nullable
    public static Profile getPreferredProfile() {
        int pos = getPreferredProfilePosition();
        if (pos < 0 || pos >= getSize())
            return null;
        return getProfile(pos);
    }
}
