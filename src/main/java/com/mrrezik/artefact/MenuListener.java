package com.mrrezik.artefact.listeners;

import com.mrrezik.artefact.Artefact;
import com.mrrezik.artefact.managers.MenuManager;
import com.mrrezik.artefact.util.ChatUtil; // ИЗМЕНЕНО
import com.mrrezik.artefact.util.NBTUtil;
import net.milkbowl.vault.economy.Economy; // НОВЫЙ ИМПОРТ
import org.black_ixx.playerpoints.PlayerPointsAPI; // НОВЫЙ ИМПОРТ
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class MenuListener implements Listener {

    private final Artefact plugin;
    // НОВЫЕ ПОЛЯ API
    private final PlayerPointsAPI playerPointsAPI;
    private final Economy vaultEconomyAPI;

    public MenuListener(Artefact plugin) {
        this.plugin = plugin;
        // Получаем API из главного класса
        this.playerPointsAPI = plugin.getPlayerPointsAPI();
        this.vaultEconomyAPI = plugin.getVaultEconomyAPI();
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        Inventory clickedInventory = event.getClickedInventory();
        Inventory topInventory = event.getView().getTopInventory();

        if (!(topInventory.getHolder() instanceof MenuManager)) {
            return;
        }

        if (clickedInventory != null && clickedInventory.equals(topInventory)) {
            event.setCancelled(true);
            MenuManager menuManager = (MenuManager) topInventory.getHolder();
            int slot = event.getSlot();
            String action = menuManager.getActionForSlot(slot);
            if (action == null) return;

            // --- ОБРАБОТКА ДЕЙСТВИЙ ---
            if (action.equals("EXCHANGE_ARTEFACT")) {
                handleExchange(player, event.getCursor());
                player.updateInventory();
            } else if (action.equalsIgnoreCase("[close]")) {
                player.closeInventory();
            } else if (action.startsWith("[message]")) {
                String message = action.substring(9).trim();
                player.sendMessage(ChatUtil.color(message)); // ИСПОЛЬЗУЕМ ChatUtil
            }
        }
    }

    /**
     * ПОЛНОСТЬЮ ПЕРЕПИСАННЫЙ МЕТОД
     * Обрабатывает обмен артефакта на ЛЮБУЮ валюту
     */
    private void handleExchange(Player player, ItemStack cursorItem) {
        // Проверка 1: Предмет в руке
        if (cursorItem == null || cursorItem.getType() == Material.AIR) {
            player.sendMessage(ChatUtil.color(plugin.getConfig().getString("messages.exchange_no_item")));
            return;
        }

        // Проверка 2: Является ли артефактом
        if (!NBTUtil.isArtefact(cursorItem)) {
            player.sendMessage(ChatUtil.color(plugin.getConfig().getString("messages.exchange_not_artefact")));
            return;
        }

        // Проверка 3: Читаем NBT
        Double value = NBTUtil.getDouble(cursorItem, NBTUtil.ARTEFACT_VALUE_KEY);
        String currencyType = NBTUtil.getString(cursorItem, NBTUtil.ARTEFACT_CURRENCY_KEY);

        if (value == null || currencyType == null) {
            player.sendMessage(ChatUtil.color(plugin.getConfig().getString("messages.exchange_no_value")));
            return;
        }

        // --- Логика начисления ---
        int pointsPerValue = plugin.getConfig().getInt("points-per-value", 100);
        int amountToGive = (int) (value * pointsPerValue);
        boolean success = false;
        String currencyName = plugin.getConfig().getString("currency-names." + currencyType, currencyType);

        // --- Выбор API для начисления ---
        if (currencyType.equals("PLAYER_POINTS")) {
            if (playerPointsAPI == null) {
                player.sendMessage(ChatUtil.color(plugin.getConfig().getString("messages.exchange_currency_not_found")
                        .replace("%type%", "PlayerPoints")));
                return;
            }
            success = playerPointsAPI.give(player.getUniqueId(), amountToGive);

        } else if (currencyType.equals("VAULT")) {
            if (vaultEconomyAPI == null) {
                player.sendMessage(ChatUtil.color(plugin.getConfig().getString("messages.exchange_currency_not_found")
                        .replace("%type%", "Vault")));
                return;
            }
            success = vaultEconomyAPI.depositPlayer(player, amountToGive).transactionSuccess();

        } else {
            player.sendMessage(ChatUtil.color(plugin.getConfig().getString("messages.exchange_currency_not_found")
                    .replace("%type%", currencyType)));
            return;
        }

        // --- Результат ---
        if (success) {
            String successMsg = plugin.getConfig().getString("messages.exchange_success");
            successMsg = successMsg.replace("%amount%", String.valueOf(amountToGive))
                    .replace("%currency%", currencyName);
            player.sendMessage(ChatUtil.color(successMsg));

            // Забираем 1 предмет с курсора
            cursorItem.setAmount(cursorItem.getAmount() - 1);
        } else {
            player.sendMessage(ChatUtil.color(plugin.getConfig().getString("messages.exchange_api_error")));
        }
    }
}