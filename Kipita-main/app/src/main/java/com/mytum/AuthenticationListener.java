package com.mytum;

public interface AuthenticationListener {
    void onTokenReceived(String auth_token);
}
