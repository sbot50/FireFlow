package de.blazemcworld.fireflow.node;

import de.blazemcworld.fireflow.FireFlow;
import de.blazemcworld.fireflow.compiler.FunctionDefinition;
import de.blazemcworld.fireflow.editor.CodeEditor;
import de.blazemcworld.fireflow.node.impl.IfNode;
import de.blazemcworld.fireflow.node.impl.ScheduleNode;
import de.blazemcworld.fireflow.node.impl.ValuesEqualNode;
import de.blazemcworld.fireflow.node.impl.WhileNode;
import de.blazemcworld.fireflow.node.impl.event.PlayerInteractEventNode;
import de.blazemcworld.fireflow.node.impl.event.PlayerJoinEventNode;
import de.blazemcworld.fireflow.node.impl.extraction.number.NumberToTextNode;
import de.blazemcworld.fireflow.node.impl.extraction.player.PlayerNameNode;
import de.blazemcworld.fireflow.node.impl.extraction.player.PlayerUUIDNode;
import de.blazemcworld.fireflow.node.impl.extraction.text.FormatTextToMessageNode;
import de.blazemcworld.fireflow.node.impl.extraction.text.TextToMessageNode;
import de.blazemcworld.fireflow.node.impl.number.AddNumbersNode;
import de.blazemcworld.fireflow.node.impl.number.DivideNumbersNode;
import de.blazemcworld.fireflow.node.impl.number.MultiplyNumbersNode;
import de.blazemcworld.fireflow.node.impl.number.SubtractNumbersNode;
import de.blazemcworld.fireflow.node.impl.number.comparison.GreaterEqualThanNode;
import de.blazemcworld.fireflow.node.impl.number.comparison.GreaterThanNode;
import de.blazemcworld.fireflow.node.impl.number.comparison.LessEqualThanNode;
import de.blazemcworld.fireflow.node.impl.number.comparison.LessThanNode;
import de.blazemcworld.fireflow.node.impl.player.*;
import de.blazemcworld.fireflow.node.impl.text.ConcatTextsNode;
import de.blazemcworld.fireflow.node.impl.variable.*;
import de.blazemcworld.fireflow.value.NumberValue;
import de.blazemcworld.fireflow.value.PlayerValue;
import de.blazemcworld.fireflow.value.TextValue;
import de.blazemcworld.fireflow.value.Value;
import it.unimi.dsi.fastutil.Pair;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Supplier;

public class NodeCategory {

    public static final NodeCategory ROOT = new NodeCategory("Root", List.of(
            () -> new ValuesEqualNode(NumberValue.INSTANCE),
            ConcatTextsNode::new,
            IfNode::new,
            PlayerInteractEventNode::new,
            PlayerJoinEventNode::new,
            ScheduleNode::new,
            WhileNode::new
    ));

    public static final NodeCategory VARIABLES = new NodeCategory("Variables", ROOT, List.of(
            () -> new GetVariableNode(LocalVariableScope.INSTANCE, NumberValue.INSTANCE),
            () -> new GetVariableNode(PersistentVariableScope.INSTANCE, NumberValue.INSTANCE),
            () -> new GetVariableNode(SpaceVariableScope.INSTANCE, NumberValue.INSTANCE),
            () -> new SetVariableNode(LocalVariableScope.INSTANCE, NumberValue.INSTANCE),
            () -> new SetVariableNode(PersistentVariableScope.INSTANCE, NumberValue.INSTANCE),
            () -> new SetVariableNode(SpaceVariableScope.INSTANCE, NumberValue.INSTANCE)
    ));

    public static final NodeCategory PLAYERS = new NodeCategory("Players", ROOT, List.of(
            ClearTitleNode::new,
            KillPlayerNode::new,
            SendActionBarNode::new,
            SendMessageNode::new,
            SendTitleNode::new,
            SetAllowPlayerFlyingNode::new,
            SetExperienceNode::new,
            SetLevelNode::new,
            SetPlayerFlyingNode::new,
            SetPlayerFoodNode::new,
            SetPlayerHealthNode::new,
            SetPlayerSaturationNode::new
    ));

    public static final NodeCategory NUMBERS = new NodeCategory("Numbers", ROOT, List.of(
            AddNumbersNode::new,
            DivideNumbersNode::new,
            GreaterEqualThanNode::new,
            GreaterThanNode::new,
            LessEqualThanNode::new,
            LessThanNode::new,
            MultiplyNumbersNode::new,
            SubtractNumbersNode::new
    ));

    public static final Map<Value, NodeCategory> EXTRACTIONS = new HashMap<>();

    public static final NodeCategory PLAYER_EXTRACTIONS = new NodeCategory("Player Extractions", PlayerValue.INSTANCE, List.of(
            PlayerUUIDNode::new,
            PlayerNameNode::new
    ));

    public static final NodeCategory TEXT_EXTRACTIONS = new NodeCategory("Text Extractions", TextValue.INSTANCE, List.of(
            FormatTextToMessageNode::new,
            TextToMessageNode::new
    ));

    public static final NodeCategory NUMBER_EXTRACTIONS = new NodeCategory("Number Extractions", NumberValue.INSTANCE, List.of(
            NumberToTextNode::new
    ));


    public final String name;
    public final @Nullable NodeCategory parent;
    public final boolean isFunctions;
    public final @Nullable Value extractionType;
    public final List<Pair<String, Supplier<Node>>> nodes = new ArrayList<>();
    public final List<NodeCategory> subcategories = new ArrayList<>();

    public NodeCategory(String name, @Nullable NodeCategory parent, boolean isFunctions, @Nullable Value extractionType, List<Supplier<Node>> nodes) {
        this.name = name;
        this.parent = parent;
        this.isFunctions = isFunctions;
        this.extractionType = extractionType;

        for (Supplier<Node> s : nodes) {
            Node node = s.get();
            if (extractionType != null) {
                if (node instanceof ExtractionNode e) {
                    if (e.input.type != extractionType) {
                        FireFlow.LOGGER.warn("Node {} is not an extraction node of type {}, but present in category {}!", node.getBaseName(), extractionType, name);
                    }
                } else {
                    FireFlow.LOGGER.warn("Node {} is not an extraction node, but present in category {}!", node.getBaseName(), name);
                }
            }
            this.nodes.add(Pair.of(node.getBaseName(), s));
        }
        this.nodes.sort(Comparator.comparing(Pair::left));

        if (parent != null && !isFunctions) {
            parent.subcategories.add(this);
            parent.subcategories.sort(Comparator.comparing(c -> c.name));
        }
        if (extractionType != null) {
            EXTRACTIONS.put(extractionType, this);
        }
    }

    public NodeCategory(String name, NodeCategory parent, List<Supplier<Node>> nodes) {
        this(name, parent, false, null, nodes);
    }

    public NodeCategory(String name, Value extractionType, List<Supplier<Node>> nodes) {
        this(name, null, false, extractionType, nodes);
    }

    public NodeCategory(String name, List<Supplier<Node>> nodes) {
        this(name, null, false, null, nodes);
    }

    public NodeCategory(String name, NodeCategory parent, boolean isFunctions, List<Supplier<Node>> nodes) {
        this(name, parent, isFunctions, null, nodes);
    }

    public static NodeCategory forFunctions(CodeEditor editor) {
        List<Supplier<Node>> nodes = new ArrayList<>();

        for (FunctionDefinition definition : editor.functions) {
            nodes.add(definition::createCall);
        }

        return new NodeCategory("Functions", ROOT, true, nodes);
    }
}
