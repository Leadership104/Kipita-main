package com.mytum.model;

import org.jetbrains.annotations.Nullable;

public class GroupNotificationItemObject {

    public String getText() {
        return Text;
    }

    public void setText(String text) {
        Text = text;
    }

    public int getNo() {
        return No;
    }

    public void setNo(int no) {
        No = no;
    }

    int No;
    String Text;

}
