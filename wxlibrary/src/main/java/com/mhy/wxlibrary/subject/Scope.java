package com.mhy.wxlibrary.subject;


import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Created By Mahongyin
 * Date    2025/3/7 14:01
 * 记录作用域下的所有观察者
 */
public class Scope {

    private CopyOnWriteArraySet<Event.DataObserver> scopeObserver = new CopyOnWriteArraySet<>();
    public <T> void registerObserver(Event.DataObserver<T> dataObserver) {
        scopeObserver.add(dataObserver);
        Event.getInstance().registerObserver(dataObserver);
    }

    /**
     * 清除作用域所以观察者
     */
    void clearObserver() {
        for (Event.DataObserver<?> observer : scopeObserver) {
            Event.getInstance().unregisterObserver(observer);
        }
        scopeObserver.clear();
    }
}
