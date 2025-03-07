package com.mhy.wxlibrary.subject;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

//实现（Subject）类
class DataSubject<T> {
    // 使用 CopyOnWriteArrayList 保证线程安全
    // 类型为T的观察者
    private final ConcurrentHashMap<Class<T>, CopyOnWriteArrayList<Event.DataObserver<T>>> objects = new ConcurrentHashMap<>();

    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    // 注册观察者
    void registerObserver(Event.DataObserver<T> observer) {
        Class<T> clazz = Event.genericType(observer.getClass());//获取泛型里的类型
        if (clazz == null) {
            return;
        }
        CopyOnWriteArrayList<Event.DataObserver<T>> objectObserver = null;
        if (objects.containsKey(clazz)) {
            objectObserver = objects.get(clazz);
        }
        if (objectObserver == null) {
            objectObserver = new CopyOnWriteArrayList<>();
        }
        if (!objectObserver.contains(observer)) {
            objectObserver.add(observer);
            objects.put(clazz, objectObserver);
        }
    }

    // 注销当前观察者
    void unregisterObserver(Event.DataObserver<T> observer) {
        Class<T> clazz = Event.genericType(observer.getClass());
        if (clazz == null) {
            return;
        }
        if (objects.containsKey(clazz)) {
            CopyOnWriteArrayList<Event.DataObserver<T>> objectObserver = objects.get(clazz);
            if (objectObserver != null) {
                objectObserver.remove(observer);
                objects.put(clazz, objectObserver);
            }
        }
    }

    // 注销所以该类型观察者
    void unregisterObserverAll(T object) {
        Class<T> clazz = (Class<T>) object.getClass();
        objects.remove(clazz);
    }

    // 更新数据并通知观察者
    void postData(T newData) {
        Class<T> clazz = (Class<T>) newData.getClass();
        // 比较类类型是否严格相同
        if (objects.containsKey(clazz)) {
            CopyOnWriteArrayList<Event.DataObserver<T>> objectObserver = objects.get(clazz);
            if (objectObserver != null) {
                notifyObservers(objectObserver, newData);
            }
        }
    }

    // 通知所有观察者（自动切换到主线程）
    private void notifyObservers(CopyOnWriteArrayList<Event.DataObserver<T>> list, T data) {
        for (Event.DataObserver<T> observer : list) {
            if (Looper.myLooper() == Looper.getMainLooper()) {
                observer.onDataChanged(data); // 已在主线程，直接调用
            } else {
                mainHandler.post(() -> observer.onDataChanged(data)); // 切换到主线程
            }
        }
    }
}