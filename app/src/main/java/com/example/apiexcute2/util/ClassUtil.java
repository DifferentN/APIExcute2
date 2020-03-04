package com.example.apiexcute2.util;

public class ClassUtil {
    public static boolean isInt(Class clazz){
        if(int.class.isAssignableFrom(clazz)||Integer.class.isAssignableFrom(clazz)){
            return true;
        }
        return false;
    }
    public static boolean isBoolean(Class clazz){
        if(boolean.class.isAssignableFrom(clazz)||Integer.class.isAssignableFrom(clazz)){
            return true;
        }
        return false;
    }
    public static boolean isString(Class clazz){
        if(String.class.isAssignableFrom(clazz)){
            return true;
        }
        return false;
    }
}
