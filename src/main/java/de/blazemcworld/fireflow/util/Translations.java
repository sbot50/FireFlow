package de.blazemcworld.fireflow.util;

import java.io.IOException;
import java.util.Properties;

public class Translations {

    private static final Properties data = new Properties();

    public static void init() {
        try {
            data.load(Translations.class.getClassLoader().getResourceAsStream("languages/" + Config.store.translations() + ".properties"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String get(String key, String... placeholders) {
        String out = data.getProperty(key);
        if (out == null) return key;
        for (int i = 0; i < placeholders.length; i++) {
            out = out.replace("{" + i + "}", placeholders[i]);
        }
        return out;
    }

}
