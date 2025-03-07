package com.mhy.socialcommon.subject;


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
        dataSubject.registerObserver(dataObserver);
    }
    /**
     * 注销单个观察者
     */
    void unregisterObserver(Event.DataObserver<T> dataObserver) {
        dataSubject.unregisterObserver(dataObserver);
    }

    /**
     * 注销所有某类型的观察者
     */
    void unregisterAll(Class<T> dataClazz) {
        dataSubject.unregisterObserverAll(dataClazz);
    }

    void postData(T data) {
        dataSubject.postData(data);
    }
}
