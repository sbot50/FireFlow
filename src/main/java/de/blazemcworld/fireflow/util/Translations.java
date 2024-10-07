package de.blazemcworld.fireflow.util;

import java.io.IOException;
import java.util.Properties;

public class Translations {

    private static final Properties data = new Properties();

    static {
        try {
            data.load(Translations.class.getClassLoader().getResourceAsStream("languages/en_US.properties"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String get(String key) {
        String out = data.getProperty(key);
        if (out == null) return key;
        return out;
    }

}
