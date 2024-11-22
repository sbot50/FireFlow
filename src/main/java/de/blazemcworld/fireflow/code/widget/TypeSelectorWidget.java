package de.blazemcworld.fireflow.code.widget;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import de.blazemcworld.fireflow.code.CodeEditor;
import de.blazemcworld.fireflow.code.Interaction;
import de.blazemcworld.fireflow.code.type.AllTypes;
import de.blazemcworld.fireflow.code.type.WireType;
import net.kyori.adventure.text.Component;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.instance.InstanceContainer;

public class TypeSelectorWidget implements Widget {

    private final BorderWidget<VerticalContainerWidget> container = new BorderWidget<>(new VerticalContainerWidget());

    public TypeSelectorWidget(List<WireType<?>> options, Consumer<WireType<?>> callback) {
        container.backgroundColor(0x99000011);
        for (WireType<?> type : options) {
            ButtonWidget button = new ButtonWidget(
                new ItemWidget(type.icon),
                new TextWidget(Component.text(type.getName()))
            );
            button.handler = interaction -> {
                if (interaction.type() != Interaction.Type.RIGHT_CLICK) return false;
                if (type.getTypeCount() == 0) {
                    callback.accept(type);
                } else {
                    selectSubtypes(type, getPos(), interaction.editor(), new ArrayList<>(), callback);
                }
                interaction.editor().rootWidgets.remove(this);
                remove();
                return true;
            };
            container.inner.widgets.add(button);
        }
    }

    private static void selectSubtypes(WireType<?> type, Vec pos, CodeEditor editor, List<WireType<?>> done, Consumer<WireType<?>> callback) {
        if (done.size() == type.getTypeCount()) {
            callback.accept(type.withTypes(done));
            return;
        }

        List<WireType<?>> filtered = new ArrayList<>();
        for (WireType<?> subtype : AllTypes.all) {
            if (type.acceptsType(subtype, done.size())) {
                filtered.add(subtype);
            }
        }

        TypeSelectorWidget selector = new TypeSelectorWidget(filtered, subtype -> {
            done.add(subtype);
            selectSubtypes(type, pos, editor, done, callback);
        });
        selector.setPos(pos);
        selector.update(editor.space.code);
        editor.rootWidgets.add(selector);
    }

    @Override
    public void setPos(Vec pos) {
        container.setPos(pos);
    }

    @Override
    public Vec getPos() {
        return container.getPos();
    }

    @Override
    public Vec getSize() {
        return container.getSize();
    }

    @Override
    public void update(InstanceContainer inst) {
        container.update(inst);
    }

    @Override
    public void remove() {
        container.remove();
    }

    @Override
    public boolean interact(Interaction i) {
        if (container.interact(i)) return true;
        if (i.type() == Interaction.Type.LEFT_CLICK) {
            remove();
            i.editor().rootWidgets.remove(this);
            return true;
        }
        return false;
    }

    @Override
    public List<Widget> getChildren() {
        return container.getChildren();
    }

}
