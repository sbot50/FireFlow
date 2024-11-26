package de.blazemcworld.fireflow.code.node;

import de.blazemcworld.fireflow.FireFlow;
import de.blazemcworld.fireflow.code.VariableStore;
import de.blazemcworld.fireflow.code.node.impl.action.player.*;
import de.blazemcworld.fireflow.code.node.impl.event.player.OnPlayerChatNode;
import de.blazemcworld.fireflow.code.node.impl.event.player.OnPlayerJoinNode;
import de.blazemcworld.fireflow.code.node.impl.flow.IfNode;
import de.blazemcworld.fireflow.code.node.impl.flow.ListForEachNode;
import de.blazemcworld.fireflow.code.node.impl.flow.RepeatNode;
import de.blazemcworld.fireflow.code.node.impl.flow.ScheduleNode;
import de.blazemcworld.fireflow.code.node.impl.list.EmptyListNode;
import de.blazemcworld.fireflow.code.node.impl.list.ListAppendNode;
import de.blazemcworld.fireflow.code.node.impl.number.*;
import de.blazemcworld.fireflow.code.node.impl.text.FormatToTextNode;
import de.blazemcworld.fireflow.code.node.impl.text.StringToTextNode;
import de.blazemcworld.fireflow.code.node.impl.variable.GetVariableNode;
import de.blazemcworld.fireflow.code.node.impl.variable.SetVariableNode;
import de.blazemcworld.fireflow.util.Translations;
import net.minestom.server.item.Material;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class NodeList {

    public static Category root;
    
    public static void init() {
        root = new Category("root", null)
            .add(new Category("action", Material.REDSTONE)
                    .add(new AdventureModeNode())
                    .add(new CreativeModeNode())
                    .add(new SendMessageNode())
                    .add(new SpectatorModeNode())
                    .add(new SurvivalModeNode())
            )
            .add(new Category("event", Material.OBSERVER)
                    .add(new OnPlayerChatNode())
                    .add(new OnPlayerJoinNode())
            )
            .add(new Category("flow", Material.COMPARATOR)
                    .add(new IfNode())
                    .add(new ListForEachNode<>(null))
                    .add(new RepeatNode())
                    .add(new ScheduleNode())
            )
            .add(new Category("list", Material.BOOKSHELF)
                    .add(new EmptyListNode<>(null))
                    .add(new ListAppendNode<>(null))
            )
            .add(new Category("number", Material.CLOCK)
                    .add(new AddNumbersNode())
                    .add(new DivideNumbersNode())
                    .add(new NumberToTextNode())
                    .add(new MultiplyNumbersNode())
                    .add(new ParseNumberNode())
                    .add(new SubtractNumbersNode())
            )
            .add(new Category("text", Material.WRITABLE_BOOK)
                    .add(new FormatToTextNode())
                    .add(new StringToTextNode())
            )
            .add(new Category("variable", Material.ENDER_CHEST)
                    .add(new GetVariableNode<>(null, VariableStore.Scope.SAVED))
                    .add(new GetVariableNode<>(null, VariableStore.Scope.SESSION))
                    .add(new GetVariableNode<>(null, VariableStore.Scope.THREAD))
                    .add(new SetVariableNode<>(null, VariableStore.Scope.SAVED))
                    .add(new SetVariableNode<>(null, VariableStore.Scope.SESSION))
                    .add(new SetVariableNode<>(null, VariableStore.Scope.THREAD))
            )
            .add(new Category("function", Material.COMMAND_BLOCK).markFunctions())
            .finish();
        
        FireFlow.LOGGER.info("Loaded " + root.collectNodes().size() + " node types");
    }

    public static class Category {
        public final String name;
        public final Material icon;
        
        public final List<Category> categories = new ArrayList<>();
        public final List<Node> nodes = new ArrayList<>();
        public boolean isFunctions = false;

        public Category(String id, Material icon) {
            name = Translations.get("category." + id);
            this.icon = icon;
        }

        public Category add(Node node) {
            nodes.add(node);
            return this;
        }

        public Category add(Category category) {
            categories.add(category);
            return this;
        }

        public Category finish() {
            for (Category category : categories) {
                category.finish();
            }
            categories.sort(Comparator.comparing(c -> c.name));
            nodes.sort(Comparator.comparing(Node::getTitle));
            return this;
        }

        public List<Node> collectNodes() {
            List<Node> list = new ArrayList<>(nodes);
            for (Category category : categories) {
                list.addAll(category.collectNodes());
            }
            return list;
        }

        public Category markFunctions() {
            isFunctions = true;
            return this;
        }
    }
}
