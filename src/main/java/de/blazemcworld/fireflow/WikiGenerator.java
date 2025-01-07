package de.blazemcworld.fireflow;

import de.blazemcworld.fireflow.code.node.Node;
import de.blazemcworld.fireflow.code.node.NodeList;
import de.blazemcworld.fireflow.util.Translations;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class WikiGenerator {

    public static void main(String[] args) {
        NodeList.init();

        Translations.use("en_US");
        generateCategory(NodeList.root, "nodes");
    }

    public static void generateCategory(NodeList.Category category, String path) {
        StringBuilder sb = new StringBuilder();
        sb.append("# ").append(Translations.get(category.name)).append("\n\n");

        if (!category.categories.isEmpty()) {
            sb.append("## Subcategories\n");
            for (NodeList.Category c : category.categories) {
                if (c.isFunctions) continue;
                generateCategory(c, path + "/" + Translations.get(c.name).toLowerCase().replace(' ', '_'));
                sb.append("- [").append(Translations.get(c.name)).append("](/").append(path).append("/").append(Translations.get(c.name).toLowerCase().replace(' ', '_')).append(".md)\n");
            }
            sb.append("\n");
        }

        for (Node node : category.nodes) {
            sb.append("## ").append(node.getTitle()).append("\n");
            sb.append(node.getWikiDescription()).append("\n\n");
        }

        try {
            Path p = Path.of("wiki/" + path + ".md");
            if (!Files.exists(p.getParent())) Files.createDirectories(p.getParent());
            Files.writeString(p, sb.toString());
        } catch (IOException e) {
            System.err.println("Failed to write " + path + ".md!");
            e.printStackTrace();
        }
    }

}
