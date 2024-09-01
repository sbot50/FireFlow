package de.blazemcworld.fireflow.node;

import de.blazemcworld.fireflow.FireFlow;
import de.blazemcworld.fireflow.compiler.FunctionDefinition;
import de.blazemcworld.fireflow.editor.CodeEditor;
import de.blazemcworld.fireflow.node.impl.IfNode;
import de.blazemcworld.fireflow.node.impl.ScheduleNode;
import de.blazemcworld.fireflow.node.impl.ValuesEqualNode;
import de.blazemcworld.fireflow.node.impl.WhileNode;
import de.blazemcworld.fireflow.node.impl.dictionary.DictionaryGetNode;
import de.blazemcworld.fireflow.node.impl.dictionary.DictionaryKeysNode;
import de.blazemcworld.fireflow.node.impl.dictionary.DictionarySetNode;
import de.blazemcworld.fireflow.node.impl.dictionary.EmptyDictionaryNode;
import de.blazemcworld.fireflow.node.impl.event.*;
import de.blazemcworld.fireflow.node.impl.extraction.enchant.EnchantID;
import de.blazemcworld.fireflow.node.impl.extraction.enchant.EnchantName;
import de.blazemcworld.fireflow.node.impl.extraction.item.*;
import de.blazemcworld.fireflow.node.impl.extraction.material.MaterialID;
import de.blazemcworld.fireflow.node.impl.extraction.material.MaterialName;
import de.blazemcworld.fireflow.node.impl.extraction.number.NumberToTextNode;
import de.blazemcworld.fireflow.node.impl.extraction.player.*;
import de.blazemcworld.fireflow.node.impl.extraction.position.*;
import de.blazemcworld.fireflow.node.impl.extraction.text.FormatTextToMessageNode;
import de.blazemcworld.fireflow.node.impl.extraction.text.TextToMessageNode;
import de.blazemcworld.fireflow.node.impl.extraction.vector.*;
import de.blazemcworld.fireflow.node.impl.item.*;
import de.blazemcworld.fireflow.node.impl.list.*;
import de.blazemcworld.fireflow.node.impl.number.*;
import de.blazemcworld.fireflow.node.impl.number.comparison.GreaterEqualThanNode;
import de.blazemcworld.fireflow.node.impl.number.comparison.GreaterThanNode;
import de.blazemcworld.fireflow.node.impl.number.comparison.LessEqualThanNode;
import de.blazemcworld.fireflow.node.impl.number.comparison.LessThanNode;
import de.blazemcworld.fireflow.node.impl.player.*;
import de.blazemcworld.fireflow.node.impl.position.CreatePositionNode;
import de.blazemcworld.fireflow.node.impl.position.PositionToVectorNode;
import de.blazemcworld.fireflow.node.impl.position.ShiftPositionVectorNode;
import de.blazemcworld.fireflow.node.impl.position.ShiftPositionXYZNode;
import de.blazemcworld.fireflow.node.impl.text.ConcatTextsNode;
import de.blazemcworld.fireflow.node.impl.variable.*;
import de.blazemcworld.fireflow.node.impl.vector.CreateVectorNode;
import de.blazemcworld.fireflow.node.impl.vector.ScaleVectorNode;
import de.blazemcworld.fireflow.node.impl.vector.VectorToPositionNode;
import de.blazemcworld.fireflow.value.*;
import it.unimi.dsi.fastutil.Pair;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Supplier;

public class NodeCategory {

    public static final NodeCategory ROOT = new NodeCategory("Root", List.of(
            () -> new ValuesEqualNode(NumberValue.INSTANCE),
            ConcatTextsNode::new,
            CreatePositionNode::new,
            CreateVectorNode::new,
            IfNode::new,
            PositionToVectorNode::new,
            ScaleVectorNode::new,
            ScheduleNode::new,
            ShiftPositionVectorNode::new,
            ShiftPositionXYZNode::new,
            VectorToPositionNode::new,
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

    public static final NodeCategory ITEMS = new NodeCategory("Items", ROOT, List.of(
            ItemBuilderNode::new,
            GiveItemNode::new,
            EnchantItemNode::new,
            RemoveEnchantItemNode::new,
            SetItemLoreNode::new,
            SetItemEnchantsNode::new,
            AddItemLoreNode::new,
            RemoveItemLoreNode::new,
            InsertItemLoreNode::new,
            SetItemAmountNode::new,
            SetItemNameNode::new,
            SetItemMaterialNode::new
    ));

    public static final NodeCategory PLAYERS = new NodeCategory("Players", ROOT, List.of(
            ClearTitleNode::new,
            DisplayPlayerDamageAnimationNode::new,
            KillPlayerNode::new,
            KnockBackPlayerNode::new,
            SendActionBarNode::new,
            SendMessageNode::new,
            SendTitleNode::new,
            SetAllowPlayerFlyingNode::new,
            SetExperienceNode::new,
            SetGamemodeNode::new,
            SetLevelNode::new,
            SetPlayerElytraFlyingNode::new,
            SetPlayerFireTicksNode::new,
            SetPlayerFlyingNode::new,
            SetPlayerFoodNode::new,
            SetPlayerHealthNode::new,
            SetPlayerSaturationNode::new,
            SetPlayerVelocityNode::new,
            TeleportPlayerNode::new
    ));

    public static final NodeCategory NUMBERS = new NodeCategory("Numbers", ROOT, List.of(
            AddNumbersNode::new,
            DivideNumbersNode::new,
            GreaterEqualThanNode::new,
            GreaterThanNode::new,
            LessEqualThanNode::new,
            LessThanNode::new,
            MultiplyNumbersNode::new,
            RandomNumberNode::new,
            SubtractNumbersNode::new
    ));

    public static final NodeCategory LISTS = new NodeCategory("Lists", ROOT, List.of(
            () -> new EmptyListNode(NumberValue.INSTANCE),
            () -> new ForeachNode(NumberValue.INSTANCE),
            () -> new ListAppendNode(NumberValue.INSTANCE),
            () -> new ListContainsNode(NumberValue.INSTANCE),
            () -> new ListFindValueNode(NumberValue.INSTANCE),
            () -> new ListGetNode(NumberValue.INSTANCE),
            () -> new ListGetNode(NumberValue.INSTANCE),
            () -> new ListInsertNode(NumberValue.INSTANCE),
            () -> new ListRemoveAtNode(NumberValue.INSTANCE),
            () -> new ListRemoveValueNode(NumberValue.INSTANCE),
            () -> new RandomListValueNode(NumberValue.INSTANCE)
    ));

    public static final NodeCategory DICTIONARIES = new NodeCategory("Dictionaries", ROOT, List.of(
            () -> new DictionaryGetNode(NumberValue.INSTANCE, NumberValue.INSTANCE),
            () -> new DictionaryKeysNode(NumberValue.INSTANCE, NumberValue.INSTANCE),
            () -> new DictionarySetNode(NumberValue.INSTANCE, NumberValue.INSTANCE),
            () -> new EmptyDictionaryNode(NumberValue.INSTANCE, NumberValue.INSTANCE)
    ));

    public static final NodeCategory EVENTS = new NodeCategory("Events", ROOT, List.of(
            PlayerChatEventNode::new,
            PlayerInteractEventNode::new,
            PlayerJoinEventNode::new,
            PlayerLeaveEventNode::new,
            PlayerPunchPlayerEventNode::new,
            PlayerSneakEventNode::new,
            PlayerStartFlyingEventNode::new,
            PlayerStopFlyingEventNode::new,
            PlayerUnsneakEventNode::new
    ));

    public static final Map<Value, NodeCategory> EXTRACTIONS = new HashMap<>();

    public static final NodeCategory PLAYER_EXTRACTIONS = new NodeCategory("Player Extractions", PlayerValue.INSTANCE, List.of(
            PlayerIsOnGroundNode::new,
            PlayerIsPlayingNode::new,
            PlayerIsSneakingNode::new,
            PlayerNameNode::new,
            PlayerPositionNode::new,
            PlayerUUIDNode::new
    ));

    public static final NodeCategory ITEM_EXTRACTIONS = new NodeCategory("Item Extractions", ItemValue.INSTANCE, List.of(
            ItemCount::new,
            ItemEnchants::new,
            ItemLore::new,
            ItemMaterial::new,
            ItemName::new
    ));

    public static final NodeCategory MATERIAL_EXTRACTIONS = new NodeCategory("Material Extractions", MaterialValue.INSTANCE, List.of(
            MaterialID::new,
            MaterialName::new
    ));

    public static final NodeCategory ENCHANT_EXTRACTIONS = new NodeCategory("Enchantment Extractions", EnchantmentValue.INSTANCE, List.of(
            EnchantID::new,
            EnchantName::new
    ));

    public static final NodeCategory TEXT_EXTRACTIONS = new NodeCategory("Text Extractions", TextValue.INSTANCE, List.of(
            FormatTextToMessageNode::new,
            TextToMessageNode::new
    ));

    public static final NodeCategory NUMBER_EXTRACTIONS = new NodeCategory("Number Extractions", NumberValue.INSTANCE, List.of(
            NumberToTextNode::new
    ));

    public static final NodeCategory VECTOR_EXTRACTIONS = new NodeCategory("Vector Extractions", VectorValue.INSTANCE, List.of(
            NormalizedVectorNode::new,
            VectorLengthNode::new,
            VectorXNode::new,
            VectorYNode::new,
            VectorZNode::new
    ));

    public static final NodeCategory POSITION_EXTRACTIONS = new NodeCategory("Position Extractions", PositionValue.INSTANCE, List.of(
            PositionFacingDirectionNode::new,
            PositionPitchNode::new,
            PositionXNode::new,
            PositionYNode::new,
            PositionYawNode::new,
            PositionZNode::new
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
