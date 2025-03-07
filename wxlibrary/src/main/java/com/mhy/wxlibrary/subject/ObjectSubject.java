package com.mhy.wxlibrary.subject;


/**
 * Created By Mahongyin
 * Date    2025/3/7 10:35
 * 一个类型一个观察者
 */
class ObjectSubject<T> {
    private DataSubject<T> dataSubject;
    //类型有多少作用域
    public ObjectSubject() {
        this.dataSubject = new DataSubject<>();
    }
    /**
     * 注册单个观察者
     */
    void registerObserver(Event.DataObserver<T> dataObserver) {
        // 注册观察者
        dataSubject.registerObserver(dataObserver);
    }
    /**
     * 注销单个观察者
     */
    void unregisterObserver(Event.DataObserver<T> dataObserver) {
        // 注销观察者
        dataSubject.unregisterObserver(dataObserver);
    }

    /**
     * 注销所有某类型的观察者
     */
    void unregisterAll(T object) {
        // 注销观察者
        dataSubject.unregisterObserverAll(object);
    }

    void postData(T data) {
        dataSubject.postData(data);
    }
}
