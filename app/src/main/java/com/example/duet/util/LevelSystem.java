package com.example.duet.util;

import android.util.Log;

public class LevelSystem {
    public static long[] expRequireList = new long[101];
    public static long[] expCumulativeList = new long[101];

    public static void initExp(){
        expRequireList[1] = 0;
        expCumulativeList[1] = 0;
        for(int i = 2; i < expRequireList.length; ++i){
            expRequireList[i] = calculationExp(i);
            expCumulativeList[i] = expRequireList[i] + expCumulativeList[i-1];
        }
    }

    public static long calculationExp(int level){
        long exp = 0;
        if(level <= 60){
            exp = Math.round((log2(level) * log2(level) * 110 + 300));
        }
        else{
            exp = Math.round((log2(level % 30 + 30) * log2(level % 30 + 30) * 110 + 300));
        }
        return exp;
    }

    public static int obtainNextPointForPost(int todayPostCount, int initPoint){
        int reflectPoint = initPoint;
        if(todayPostCount > 3){
            return 15;
        }
        for(int i=0; i<todayPostCount; ++i){
            reflectPoint /= 2;
        }
        return reflectPoint;
    }

    private static double log2(int x){
        return Math.log10(x) / Math.log10(2);
    }
}
