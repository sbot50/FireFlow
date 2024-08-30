package de.blazemcworld.fireflow.util;

public class CamelCase {

    public static String camelCase(String input) {
        StringBuilder sb = new StringBuilder();
        String[] words = input.split(" ");
        for (int i = 0; i < words.length; i++) {
            String word = words[i];
            if (i > 0) sb.append(" ");
            sb.append(Character.toUpperCase(word.charAt(0)));
            sb.append(word.substring(1));
        }
        return sb.toString();
    }

}
