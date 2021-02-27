package com.k.utils.config;

import org.ini4j.Ini;

import java.io.File;

public class FileReader {

    private static final String FILE_PATH=System.getProperty("user.dir") + "/src/main/resources/pageobjects/";
    private static final String FILE_NAME="PageObjects.ini";

    public  String strReadIniFile(String strHeader, String strKeys){
        Ini ini = null;
        try {
            ini = new Ini(new File(FILE_PATH+FILE_NAME));

        } catch (Exception e) {

        }
        return ini.get(strHeader, strKeys);
    }

}
