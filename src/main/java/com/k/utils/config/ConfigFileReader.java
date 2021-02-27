package com.k.utils.config;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Properties;

@Getter
@Setter
public class ConfigFileReader {

    //Default values
    public boolean isRemote = false;
    public int parallelThreadCount = 1;
    public String environment = "qa";
    public int maxTimeout = 30;
    private static ConfigFileReader configFileReader;

    private ConfigFileReader() { }

    public static ConfigFileReader getInstance() {
        if (configFileReader == null) {
            configFileReader = new ConfigFileReader();
        }
        return configFileReader;
    }

    public ConfigFileReader readConfig() {
        try {
            Properties prop = new Properties();
            String propFileName = "config.properties";
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream(propFileName);

            if (inputStream != null) {
                prop.load(inputStream);
            } else {
                throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath"); }
            if (getValue(prop, "parallel-thread") != null)
                this.parallelThreadCount = Integer.parseInt(getValue(prop, "parallel-thread"));
            if (getValue(prop, "isRemote") != null)
                this.isRemote = Boolean.parseBoolean(getValue(prop, "isRemote"));
            if (getValue(prop, "env") != null)
                this.environment = getValue(prop, "env");
            if (getValue(prop, "maxTimeout") != null)
                this.maxTimeout = Integer.parseInt(getValue(prop, "maxTimeout"));
        } catch (Exception e) {
            System.out.println("Something went wrong while reading the property file");
        }
        return configFileReader;
    }

    //This will check commandline properties first, if not available then check for properties file
    private String getValue(Properties prop, String key) {
        if (StringUtils.isNotEmpty(System.getProperty(key)))
            return System.getProperty(key);
        else if (StringUtils.isNotEmpty(prop.getProperty(key))) {
            return prop.getProperty(key);
        }
        return null;
    }
}
