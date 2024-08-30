package de.blazemcworld.fireflow.util;

import net.minestom.server.utils.NamespaceID;

public class TextCase {

    public static String namespaceToName(NamespaceID id) {
        String path = id.path();
        path = path.replaceAll("_", " ");
        return camelCase(path);
    }

    public static String camelCase(String input) {
        StringBuilder sb = new StringBuilder();
        String[] words = input.split(" ");
        for (int i = 0; i < words.length; i++) {
            String word = words[i];
            if (i > 0) {
                sb.append(" ");
            }
            char first = word.charAt(0);
            char upper = Character.toUpperCase(first);
            sb.append(upper);
            String rest = word.substring(1);
            sb.append(rest);
        }
        return sb.toString();
    }

}
