package dev.twice.kits.commands;

import dev.twice.kits.KitsPlugin;
import dev.twice.kits.managers.KitManager;
import dev.twice.kits.objects.KitData;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KitsCommand implements CommandExecutor, TabCompleter {

    private final Map<String, CommandHandler> subcommands = new HashMap<>();

    public KitsCommand() {
        subcommands.put("create", this::createKit);
        subcommands.put("delete", this::deleteKit);
        subcommands.put("help", (sender, args) -> help(sender));
        subcommands.put("reload", (sender, args) -> reload(sender));
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String @NotNull [] args) {
        if (args.length < 1) {
            String kits = String.join(", ", KitManager.getAllowedKits(sender));
            if (kits.isEmpty()) {
                sender.sendMessage(KitsPlugin.getMessage("lang.kits-no-available"));
            } else {
                sender.sendMessage(KitsPlugin.getMessage("lang.kits-list").replace("{kits}", kits));
            }
            return true;
        }

        if (hasCommand(args[0])) {
            CommandHandler handler = getCommand(args[0]);
            handler.onCommand(sender, Arrays.copyOfRange(args, 1, args.length));
            return true;
        }

        sender.sendMessage(KitsPlugin.getMessage("lang.unknown-command"));
        return true;
    }

    private CommandHandler getCommand(String subcommand) {
        return subcommands.get(subcommand.toLowerCase());
    }

    private boolean hasCommand(String subcommand) {
        return subcommands.containsKey(subcommand.toLowerCase());
    }

    private void createKit(@NotNull CommandSender sender, String @NotNull [] args) {
        if (!sender.hasPermission("kits.admin.create")) {
            sender.sendMessage(KitsPlugin.getMessage("lang.no-permission"));
            return;
        }

        if (args.length < 1) {
            sender.sendMessage(KitsPlugin.getMessage("lang.kits-create-error"));
            return;
        }

        if (!(sender instanceof Player player)) {
            sender.sendMessage(KitsPlugin.getMessage("lang.player-only"));
            return;
        }

        String kit = args[0];

        if (KitManager.getKit(kit) != null) {
            KitsPlugin.getDatabase().deleteLayouts(kit);
        }

        KitData kitData = new KitData(player.getInventory(), kit);
        KitManager.addKit(kitData);
        KitsPlugin.getDatabase().insertKit(kitData);
        sender.sendMessage(KitsPlugin.getMessage("lang.kit-created"));
    }

    private void deleteKit(@NotNull CommandSender sender, String @NotNull [] args) {
        if (!sender.hasPermission("kits.admin.delete")) {
            sender.sendMessage(KitsPlugin.getMessage("lang.no-permission"));
            return;
        }

        if (args.length < 1) {
            sender.sendMessage(KitsPlugin.getMessage("lang.kits-delete-error"));
            return;
        }

        String kit = args[0];
        KitManager.delKit(kit);
        KitsPlugin.getDatabase().deleteKit(kit);
        KitsPlugin.getDatabase().deleteLayouts(kit);
        sender.sendMessage(KitsPlugin.getMessage("lang.kit-deleted"));
    }

    private void help(@NotNull CommandSender sender) {
        if (!sender.hasPermission("kits.help")) {
            sender.sendMessage(KitsPlugin.getMessage("lang.no-permission"));
            return;
        }

        List<String> lines = KitsPlugin.getConf().getStringList("lang.help-list");
        for (String line : lines) {
            sender.sendMessage(line.replace("&", "§").replace("{prefix}", KitsPlugin.getPrefix()));
        }
    }

    private void reload(@NotNull CommandSender sender) {
        if (!sender.hasPermission("kits.admin.reload")) {
            sender.sendMessage(KitsPlugin.getMessage("lang.no-permission"));
            return;
        }

        KitsPlugin.getInstance().reloadConfig();
        sender.sendMessage(KitsPlugin.getMessage("lang.reloaded"));
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String @NotNull [] args) {
        if (args.length != 1) {
            return null;
        }

        List<String> allowedCommands = new ArrayList<>();
        for (String subcommand : subcommands.keySet()) {
            if (sender.hasPermission("kits.admin." + subcommand) ||
                    (subcommand.equalsIgnoreCase("help") && sender.hasPermission("kits.help"))) {
                allowedCommands.add(subcommand);
            }
        }

        List<String> matchedCommands = new ArrayList<>();
        StringUtil.copyPartialMatches(args[0], allowedCommands, matchedCommands);
        Collections.sort(matchedCommands);
        return matchedCommands;
    }

    @FunctionalInterface
    private interface CommandHandler {
        void onCommand(CommandSender sender, String[] args);
    }
}