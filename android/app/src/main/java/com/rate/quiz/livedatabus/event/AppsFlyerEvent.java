package com.rate.quiz.livedatabus.event;

import java.util.HashMap;
import java.util.Map;

public class AppsFlyerEvent {

    public static final String KEY = "AppsFlyerEvent";

    public boolean success;
    public Map<String, Object> map;
    public String error;

    public static AppsFlyerEvent success(Map<String, Object> map) {
        AppsFlyerEvent event = new AppsFlyerEvent();
        event.success = true;
        event.map = new HashMap<>(map);
        return event;
    }

    public static AppsFlyerEvent error(String s) {
        AppsFlyerEvent event = new AppsFlyerEvent();
        event.success = false;
        event.error = s;
        return event;
    }
}
