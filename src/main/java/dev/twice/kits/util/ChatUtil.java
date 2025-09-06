package dev.twice.kits.util;

import lombok.experimental.UtilityClass;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.jetbrains.annotations.NotNull;

@UtilityClass
public class ChatUtil {
    private static final LegacyComponentSerializer LEGACY_SERIALIZER =
            LegacyComponentSerializer.legacyAmpersand();

    @NotNull
    public static String color(@NotNull String text) {
        text = RGBUtils.toChatColorString(text);
        Component component = LEGACY_SERIALIZER.deserialize(text);
        return LegacyComponentSerializer.legacySection().serialize(component);
    }

    @NotNull
    public static Component colorComponent(@NotNull String text) {
        text = RGBUtils.toChatColorString(text);
        return LEGACY_SERIALIZER.deserialize(text);
    }
}