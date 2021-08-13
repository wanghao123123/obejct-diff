package com.example.objectdiff.utils;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.function.Consumer;

/**
 * @auther wsdc
 * @date 2021/5/10
 */
@Component
public class SpringContext implements ApplicationContextAware, DisposableBean {
    transient static ApplicationContext context;
    private static List<Consumer<ApplicationContext>> registerList = new ArrayList<>();
    private static Map<Class,Object> cacheMap = new ConcurrentHashMap<>();

    //  暂定一个线程池  可能会迁移到其他地方  不固定长度
    private static ExecutorService tpe = new ThreadPoolExecutor(24,24,30,
            TimeUnit.SECONDS,new LinkedBlockingQueue<>());

    public static void register(Consumer<ApplicationContext> consumer){
        if(consumer == null){
            return ;
        }
        if(context == null){
            registerList.add(consumer);
            return ;
        }
        consumer.accept(context);
    }

    public static <T>Map<String,? extends T> queryBeanMap(Class<? extends T> clz){
        if(context == null){
            return null;
        }

        Object cache = cacheMap.get(clz);
        if(cache != null){
            return ((Map<String, T>) cache);
        }

        Map<String, ? extends T> beansOfType = context.getBeansOfType(clz);
        cacheMap.putIfAbsent(clz,beansOfType);

        return beansOfType;
    }

    public static <T> T queryBeanMapByName(Class<? extends T> clz,String name ){
        Map<String, ? extends T> stringMap = queryBeanMap(clz);
        if(stringMap == null){
            return null;
        }
        return stringMap.get(name);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
        for (Consumer<ApplicationContext> consumer : registerList) {
            consumer.accept(context);
        }
        registerList.clear();
    }

    @Override
    public void destroy() throws Exception {
        tpe.shutdown();
    }

    public static ApplicationContext context(){
        return context;
    }

    public static ExecutorService executor(){
        return tpe;
    }
}
