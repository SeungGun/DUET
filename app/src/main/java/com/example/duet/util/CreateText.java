package com.example.duet.util;

import java.util.ArrayList;

/**
 * @auther Me
 * @since 2022/05/03 10:47 오후
 Create random String array for testing
 **/

public class CreateText {

    public ArrayList<String> generateRandomChunk() {
        ArrayList<String> ret = new ArrayList<>();
        String tempString = "";

        for (int i = 0; i < 5; i++){
            char temp = (char)((int)(Math.random() * 90) + 65);
            tempString = tempString + temp;
            ret.add(tempString);

        }

        return ret;
    }

}
