package com.mhy.socialcommon;

import android.os.Bundle;
import android.text.TextUtils;

import java.util.ArrayList;

public class ShareEntity {

    public Bundle params;
    private SocialType type;

    public ShareEntity(SocialType type) {
        this.type = type;
        this.params = new Bundle();
    }

    protected static void addParams(Bundle params, String key, String value) {
        if (params == null || TextUtils.isEmpty(key) || TextUtils.isEmpty(value)) {
            return;
        }
        params.putString(key, value);
    }

    protected static void addParams(Bundle params, String key, int value) {
        if (params == null || TextUtils.isEmpty(key)) {
            return;
        }
        params.putInt(key, value);
    }

    protected static void addParams(Bundle params, String key, ArrayList<String> value) {
        if (params == null || TextUtils.isEmpty(key) || value == null || value.size() == 0) {
            return;
        }
        params.putStringArrayList(key, value);
    }

    public Bundle getParams() {
        return params;
    }

    public void setParams(Bundle params) {
        this.params = params;
    }

    public SocialType getType() {
        return type;
    }
}
