package de.blazemcworld.fireflow.code.type;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.minimessage.tag.standard.StandardTags;
import net.minestom.server.item.Material;

public class TextType extends WireType<Component> {

    public static final TextType INSTANCE = new TextType();

    public static final MiniMessage MM = MiniMessage.builder()
            .tags(TagResolver.builder().resolvers(
                    StandardTags.color(),
                    StandardTags.decorations(),
                    StandardTags.font(),
                    StandardTags.gradient(),
                    StandardTags.keybind(),
                    StandardTags.newline(),
                    StandardTags.rainbow(),
                    StandardTags.reset(),
                    StandardTags.transition(),
                    StandardTags.translatable()
            ).build()).build();

    private TextType() {
        super("text", NamedTextColor.LIGHT_PURPLE, Material.BOOK);
    }

    @Override
    public Component defaultValue() {
        return Component.empty();
    }

    @Override
    public Component parseInset(String str) {
        return MM.deserialize(str);
    }

    @Override
    protected String stringifyInternal(Component value) {
        return MM.serialize(value);
    }

    @Override
    public Component convert(Object obj) {
        if (obj instanceof Component comp) return comp;
        return null;
    }

    @Override
    public JsonElement toJson(Component obj) {
        return new JsonPrimitive(MM.serialize(obj));
    }

    @Override
    public Component fromJson(JsonElement json) {
        return MM.deserialize(json.getAsString());
    }
}
