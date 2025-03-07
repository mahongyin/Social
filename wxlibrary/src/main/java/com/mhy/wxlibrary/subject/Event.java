package com.mhy.wxlibrary.subject;


import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created By Mahongyin
 * Date    2025/2/26 12:53
 */
public class Event {
    private static Event instance;
    /**
     * 类型对应的被观察者
     */
    private final ConcurrentHashMap<Class, ObjectSubject> objects = new ConcurrentHashMap<>();
    /**
     * 作用域对应的被观察者
     */
    private final ConcurrentHashMap<Integer, Scope> scopes = new ConcurrentHashMap<>();

    public static Event getInstance() {
        if (instance == null) {
            synchronized (Event.class) {
                if (instance == null) {
                    instance = new Event();
                }
            }
        }
        return instance;
    }

    /**
     * 注册单个观察者
     */
     <T> void registerObserver(Event.DataObserver<T> dataObserver) {
        ObjectSubject dataSubject = null;
        Class<T> clazz = genericType(dataObserver.getClass());//获取泛型里的类型
        if (objects.containsKey(clazz)) {//有就直接用，一种类型只能对应1个dataObserver
            dataSubject = objects.get(clazz);
        }
        if (dataSubject == null) {
            dataSubject = new ObjectSubject<>();
            objects.put(clazz, dataSubject);
        }
        dataSubject.registerObserver(dataObserver);
    }

    /**
     * 注销单个观察者
     */
     <T> void unregisterObserver(Event.DataObserver dataObserver) {
        ObjectSubject dataSubject = null;
        Class<T> clazz = genericType(dataObserver.getClass());//获取泛型里的类型
        if (objects.containsKey(clazz)) {
            dataSubject = objects.get(clazz);
            if (dataSubject != null) {
                dataSubject.unregisterObserver(dataObserver);
            }
        }
    }
    /**
     * @param context Activity / Fragment
     */
    public Scope register(Object context) {
        int hashCode = context.hashCode();
        if (scopes.containsKey(hashCode)) {
            return scopes.get(hashCode);
        } else {
            Scope scope = new Scope();
            scopes.put(hashCode, scope);
            return scope;
        }
    }

    /**
     * @param context Activity / Fragment
     */
    public void unregister(Object context) {
        int hashCode = context.hashCode();
        if (scopes.containsKey(hashCode)) {
            Scope scope = scopes.get(hashCode);
            if (scope != null) {
                scope.clearObserver();
            }
            scopes.remove(hashCode);
        }
    }

    public void postData(Object data) {
        ObjectSubject dataSubject = null;
        if (objects.containsKey(data.getClass())) {
            dataSubject = objects.get(data.getClass());
        }
        if (dataSubject != null) {
            dataSubject.postData(data);
        }
    }

    /**
     * 泛型里获取类型
     */
    static <T> Class<T> genericType(Class<?> clazz) {
        Type[] generic = clazz.getGenericInterfaces();
        ParameterizedType parameterizedType = (ParameterizedType) generic[0];//就1个
        Type[] types = parameterizedType.getActualTypeArguments();
        return (Class<T>) types[0];//就1个
    }

    //定义观察者接口
    public interface DataObserver<T> {
        void onDataChanged(T data);
    }
}
