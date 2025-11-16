package com.mrrezik.artefact.commands;

import com.mrrezik.artefact.Artefact;
import com.mrrezik.artefact.managers.ArtefactManager;
import com.mrrezik.artefact.util.ChatUtil; // ИЗМЕНЕНО
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ArtefactCommand implements CommandExecutor, TabCompleter {

    private final Artefact plugin;
    private final ArtefactManager artefactManager;

    public ArtefactCommand(Artefact plugin) {
        this.plugin = plugin;
        this.artefactManager = plugin.getArtefactManager();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }

        String subCommand = args[0].toLowerCase();

        switch (subCommand) {
            case "help":
                sendHelp(sender);
                break;
            case "open":
                handleOpen(sender);
                break;
            case "give":
                handleGive(sender, args);
                break;
            case "reload":
                handleReload(sender, args);
                break;
            default:
                sendHelp(sender); // Показываем помощь при неверной команде
                break;
        }
        return true;
    }

    // ИЗМЕНЕНО: Используем ChatUtil
    private void sendHelp(CommandSender sender) {
        List<String> helpMessages = plugin.getConfig().getStringList("help-message");
        for (String line : helpMessages) {
            sender.sendMessage(ChatUtil.color(line));
        }
    }

    // ИЗМЕНЕНО: Используем ChatUtil и конфиг
    private void handleOpen(CommandSender sender) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatUtil.color(plugin.getConfig().getString("command.only_player")));
            return;
        }
        Player player = (Player) sender;
        player.openInventory(plugin.getMenuManager().getMenuInventory());
        player.sendMessage(ChatUtil.color(plugin.getConfig().getString("command.menu_opened")));
    }

    // ИЗМЕНО: Используем ChatUtil и конфиг
    private void handleGive(CommandSender sender, String[] args) {
        if (!sender.hasPermission("artefacts.admin.give")) {
            sender.sendMessage(ChatUtil.color(plugin.getConfig().getString("command.no_permission")));
            return;
        }

        // /art give <name> <player> <amount>
        if (args.length != 4) {
            sender.sendMessage(ChatUtil.color(plugin.getConfig().getString("command.give_usage")));
            return;
        }

        String artefactId = args[1];
        Player target = Bukkit.getPlayer(args[2]);
        if (target == null) {
            String msg = plugin.getConfig().getString("command.player_not_found").replace("%player%", args[2]);
            sender.sendMessage(ChatUtil.color(msg));
            return;
        }

        int amount;
        try {
            amount = Integer.parseInt(args[3]);
            if (amount <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            sender.sendMessage(ChatUtil.color(plugin.getConfig().getString("command.invalid_amount")));
            return;
        }

        ItemStack artefactItem = artefactManager.getArtefact(artefactId, amount);
        if (artefactItem == null) {
            String msg = plugin.getConfig().getString("command.artefact_not_found").replace("%id%", artefactId);
            sender.sendMessage(ChatUtil.color(msg));
            return;
        }

        target.getInventory().addItem(artefactItem);
        String msgSender = plugin.getConfig().getString("command.give_success_sender").replace("%player%", target.getName());
        sender.sendMessage(ChatUtil.color(msgSender));
        target.sendMessage(ChatUtil.color(plugin.getConfig().getString("command.give_success_target")));
    }

    // ИЗМЕНО: Используем ChatUtil и конфиг
    private void handleReload(CommandSender sender, String[] args) {
        if (!sender.hasPermission("artefacts.admin.reload")) {
            sender.sendMessage(ChatUtil.color(plugin.getConfig().getString("command.no_permission")));
            return;
        }

        if (args.length == 1 || (args.length == 2 && args[1].equalsIgnoreCase("all"))) {
            plugin.reloadPlugin();
            sender.sendMessage(ChatUtil.color(plugin.getConfig().getString("command.reload_all")));
        } else if (args.length == 2) {
            if (args[1].equalsIgnoreCase("config")) {
                plugin.reloadConfig();
                plugin.getArtefactManager().loadArtefacts();
                sender.sendMessage(ChatUtil.color(plugin.getConfig().getString("command.reload_config")));
            } else if (args[1].equalsIgnoreCase("menu")) {
                plugin.getMenuManager().loadMenu();
                sender.sendMessage(ChatUtil.color(plugin.getConfig().getString("command.reload_menu")));
            } else {
                sender.sendMessage(ChatUtil.color(plugin.getConfig().getString("command.reload_usage")));
            }
        } else {
            sender.sendMessage(ChatUtil.color(plugin.getConfig().getString("command.reload_usage")));
        }
    }


    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            completions.addAll(Arrays.asList("open", "give", "help", "reload"));
        } else if (args.length > 1) {
            String subCommand = args[0].toLowerCase();
            if (subCommand.equals("give")) {
                if (args.length == 2) { // <name>
                    completions.addAll(artefactManager.getArtefactIds());
                } else if (args.length == 3) { // <player>
                    completions.addAll(Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList()));
                } else if (args.length == 4) { // <amount>
                    completions.addAll(Arrays.asList("1", "16", "64"));
                }
            } else if (subCommand.equals("reload")) {
                if (args.length == 2) {
                    completions.addAll(Arrays.asList("all", "config", "menu"));
                }
            }
        }
        return StringUtil.copyPartialMatches(args[args.length - 1], completions, new ArrayList<>());
    }
}