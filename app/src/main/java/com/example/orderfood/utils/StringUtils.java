package com.example.orderfood.utils;

public class StringUtils {
    public static  boolean isEmpty(String input){
        return  input==null||input.isEmpty()||("").equals(input.trim());
    }
}
