package dev.twice.kits.util;

import lombok.experimental.UtilityClass;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.ChatColor;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@UtilityClass
public class RGBUtils {
    private static final Pattern HEX_PATTERN = Pattern.compile("#[0-9a-fA-F]{6}");
    private static final Pattern FIX3_PATTERN = Pattern.compile("\\&x[\\&0-9a-fA-F]{12}");

    @NotNull
    public static String toChatColor(@NotNull String hexCode) {
        StringBuilder magic = new StringBuilder("§x");
        char[] hexChars = hexCode.substring(1).toCharArray();

        for (char c : hexChars) {
            magic.append('§').append(c);
        }

        return magic.toString();
    }

    @NotNull
    public static String stripColor(@NotNull String str) {
        String text = applyFormats(str);

        Matcher matcher = HEX_PATTERN.matcher(text);
        while (matcher.find()) {
            String hexcode = matcher.group();
            text = text.replace(hexcode, "");
        }

        text = ChatColor.translateAlternateColorCodes('&', text);
        return text.replaceAll("§.", "");
    }

    @NotNull
    public static String toChatColorString(@NotNull String textInput) {
        String text = applyFormats(textInput);

        Matcher matcher = HEX_PATTERN.matcher(text);
        while (matcher.find()) {
            String hexcode = matcher.group();
            text = text.replace(hexcode, toChatColor(hexcode));
        }

        return text;
    }

    @NotNull
    private static String fixFormat1(@NotNull String text) {
        return text.replace("&#", "#");
    }

    @NotNull
    private static String fixFormat3(@NotNull String text) {
        text = text.replace('§', '&');

        Matcher matcher = FIX3_PATTERN.matcher(text);
        while (matcher.find()) {
            String hexcode = matcher.group();
            String fixed = String.valueOf(new char[]{
                    hexcode.charAt(3), hexcode.charAt(5), hexcode.charAt(7),
                    hexcode.charAt(9), hexcode.charAt(11), hexcode.charAt(13)
            });
            text = text.replace(hexcode, "#" + fixed);
        }

        return text;
    }

    @NotNull
    private static String applyFormats(@NotNull String text) {
        text = fixFormat1(text);
        return fixFormat3(text);
    }
}