package com.xmcc.utils;

public class LevelUtil {
    public static final String ROOT = "0";
    private static final String SEPARATOR = ".";

    public static String contact(String parentLevel,Integer parentId){
        if (parentLevel == null){
            return ROOT;
        }
        return new StringBuffer(parentLevel).append(SEPARATOR).append(parentId).toString();
    }
}
