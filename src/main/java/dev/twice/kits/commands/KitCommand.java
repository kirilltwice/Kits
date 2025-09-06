// KitCommand.java
package dev.twice.kits.commands;

import dev.twice.kits.KitsPlugin;
import dev.twice.kits.gui.impl.EditorGui;
import dev.twice.kits.gui.impl.PreviewGui;
import dev.twice.kits.managers.EditorManager;
import dev.twice.kits.managers.KitManager;
import dev.twice.kits.objects.KitData;
import dev.twice.kits.objects.Layout;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class KitCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String @NotNull [] args) {
        if (args.length < 1) {
            sender.sendMessage(KitsPlugin.getMessage("lang.kit-error"));
            return true;
        }

        if (args[0].equalsIgnoreCase("edit")) {
            return handleEdit(sender, Arrays.copyOfRange(args, 1, args.length));
        }

        if (args[0].equalsIgnoreCase("preview")) {
            return handlePreview(sender, Arrays.copyOfRange(args, 1, args.length));
        }

        return handleGive(sender, args);
    }

    private boolean handleEdit(@NotNull CommandSender sender, String @NotNull [] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(KitsPlugin.getMessage("lang.player-only"));
            return true;
        }

        if (!sender.hasPermission("kits.edit")) {
            sender.sendMessage(KitsPlugin.getMessage("lang.no-permission"));
            return true;
        }

        if (EditorManager.isEditor(player)) {
            return true;
        }

        if (args.length < 1) {
            sender.sendMessage(KitsPlugin.getMessage("lang.edit-error"));
            return true;
        }

        String kitName = args[0];
        KitData kitData = KitManager.getKit(kitName);
        if (kitData == null) {
            sender.sendMessage(KitsPlugin.getMessage("lang.kit-not-found"));
            return true;
        }

        if (!sender.hasPermission("kits.kit." + kitName)) {
            sender.sendMessage(KitsPlugin.getMessage("lang.edit-no-access"));
            return true;
        }

        Layout layout = kitData.getLayout(sender.getName());
        ItemStack[] contents = layout == null ? kitData.getContents() : layout.getContents();

        EditorGui gui = new EditorGui(player, kitName, contents);
        EditorManager.setEditor(player, gui);
        gui.open();
        sender.sendMessage(KitsPlugin.getMessage("lang.edit-open"));
        return true;
    }

    private boolean handlePreview(@NotNull CommandSender sender, String @NotNull [] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(KitsPlugin.getMessage("lang.player-only"));
            return true;
        }

        if (!sender.hasPermission("kits.preview")) {
            sender.sendMessage(KitsPlugin.getMessage("lang.no-permission"));
            return true;
        }

        if (EditorManager.isEditor(player)) {
            return true;
        }

        if (args.length < 1) {
            sender.sendMessage(KitsPlugin.getMessage("lang.preview-error"));
            return true;
        }

        String kitName = args[0];
        KitData kitData = KitManager.getKit(kitName);
        if (kitData == null) {
            sender.sendMessage(KitsPlugin.getMessage("lang.kit-not-found"));
            return true;
        }

        PreviewGui gui = new PreviewGui(player, kitData);
        gui.open();
        sender.sendMessage(KitsPlugin.getMessage("lang.preview-open").replace("{kit}", kitName));
        return true;
    }

    private boolean handleGive(@NotNull CommandSender sender, String @NotNull [] args) {
        String kitName = args[0];
        KitData kitData = KitManager.getKit(kitName);

        if (kitData == null) {
            sender.sendMessage(KitsPlugin.getMessage("lang.kit-not-found"));
            return true;
        }

        if (!sender.hasPermission("kits.kit." + kitName)) {
            sender.sendMessage(KitsPlugin.getMessage("lang.kit-no-access"));
            return true;
        }

        if (sender instanceof Player && !KitsPlugin.checkCooldown(sender.getName())) {
            sender.sendMessage(KitsPlugin.getMessage("lang.cooldown"));
            return true;
        }

        String target = sender.getName();
        if (args.length > 1) {
            if (!sender.hasPermission("kits.give")) {
                sender.sendMessage(KitsPlugin.getMessage("lang.no-permission"));
                return true;
            }
            target = args[1];
        }

        Player player = Bukkit.getPlayer(target);
        if (player == null) {
            sender.sendMessage(KitsPlugin.getMessage("lang.player-not-found"));
            return true;
        }

        Layout layout = kitData.getLayout(player.getName());
        kitData.save(player.getInventory(), layout);
        sender.sendMessage(KitsPlugin.getMessage("lang.kit-received"));
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String @NotNull [] args) {
        if (args.length == 1) {
            Set<String> allowedKits = KitManager.getAllowedKits(sender);
            allowedKits.add("edit");
            allowedKits.add("preview");
            return match(args[0], allowedKits);
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("edit") && sender.hasPermission("kits.edit")) {
            return match(args[1], KitManager.getAllowedKits(sender));
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("preview") && sender.hasPermission("kits.preview")) {
            return match(args[1], KitManager.getKits().keySet());
        }

        return null;
    }

    private List<String> match(String arg, Set<String> list) {
        List<String> matched = new ArrayList<>();
        StringUtil.copyPartialMatches(arg, list, matched);
        Collections.sort(matched);
        return matched;
    }
}