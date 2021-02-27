package com.k.utils.config;

import org.ini4j.Ini;

import java.io.File;
import java.util.function.Supplier;

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

    static boolean waitUntilCondition(Supplier<Boolean> function) {
        Double timer = 0.0;
        Double maxTimeOut = 200.0;

        boolean isFound;
        do {
            isFound = function.get();
            if (isFound) {
                break;
            } else {
                try {
                    Thread.sleep(5000); // Sleeping for 5 sec
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                timer++;
                System.out.println("Waiting for condition to be true .. waited .." + timer * 5 + " sec.");
            }
        } while (timer < maxTimeOut + 1.0);

        return isFound;
    }

}
