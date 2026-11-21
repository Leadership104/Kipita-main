package com.mytum.view;

public interface MainView {
    void hideLoadingLayout();

    void showFailure(String str);

    void showLoadingLayout();

    void showSuccess(Object obj);
}
