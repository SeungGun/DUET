package com.example.duet.util;

import java.util.ArrayList;

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
