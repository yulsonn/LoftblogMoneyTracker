package ru.loftschool.loftblogmoneytracker.utils.google;


public interface GoogleScopes {

    String G_PLUS_SCOPE    = "oauth2:https://www.googleapis.com/auth/plus.me";
    String USERINFO_SCOPE  = "https://www.googleapis.com/auth/userinfo.profile";
    String EMAIL_SCOPE     = "https://www.googleapis.com/auth/userinfo.email";
    String SCOPES = G_PLUS_SCOPE + " " + USERINFO_SCOPE + " " + EMAIL_SCOPE;
}
