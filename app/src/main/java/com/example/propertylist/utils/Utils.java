package com.example.propertylist.utils;


public class Utils {
    public static boolean isNotMissing(String s){
      if(s != null && s.length() > 0) {
        return true;
      }else
        return false;
    }
}