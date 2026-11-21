package com.mytum.interfaces;

import com.mytum.model.ImageItemObject;

public interface ComplaintClickPressed {
    void onClickEvent(int postion);
    void onCallEvent(int postion);
    void onSiteUrl(int postion);
    void onImageClick(int postion);
    void onAddressClick(int postion);
    void onMoreInfo(int postion);
}
