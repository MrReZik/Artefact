package com.mrrezik.artefact.managers;

import com.mrrezik.artefact.Artefact;
import com.mrrezik.artefact.util.ChatUtil; // ИЗМЕНЕНО
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MenuManager implements InventoryHolder {

    private final Artefact plugin;
    private Inventory menuInventory;
    private String menuTitle;
    private int menuSize;

    private final Map<Integer, String> slotActions = new HashMap<>();

    public MenuManager(Artefact plugin) {
        this.plugin = plugin;
        loadMenu();
    }

    public void loadMenu() {
        File menuFile = new File(plugin.getDataFolder(), "menu.yml");
        if (!menuFile.exists()) {
            plugin.getLogger().warning("menu.yml не найден! Меню не будет работать.");
            return;
        }

        FileConfiguration menuConfig = YamlConfiguration.loadConfiguration(menuFile);
        ConfigurationSection menuSection = menuConfig.getConfigurationSection("menu");
        if (menuSection == null) return;

        // ИЗМЕНЕНО: Используем ChatUtil
        this.menuTitle = ChatUtil.color(menuSection.getString("title", "Меню"));
        this.menuSize = menuSection.getInt("size", 27);
        this.menuInventory = Bukkit.createInventory(this, menuSize, menuTitle);
        this.slotActions.clear();

        ConfigurationSection itemsSection = menuSection.getConfigurationSection("items");
        if (itemsSection == null) return;

        for (String itemKey : itemsSection.getKeys(false)) {
            ConfigurationSection itemConfig = itemsSection.getConfigurationSection(itemKey);
            if (itemConfig == null) continue;

            Material material = Material.matchMaterial(itemConfig.getString("material", "STONE"));
            if (material == null) material = Material.STONE;

            ItemStack item = new ItemStack(material);
            ItemMeta meta = item.getItemMeta();

            // ИЗМЕНЕНО: Используем ChatUtil
            String displayName = ChatUtil.color(itemConfig.getString("display_name", " "));
            meta.setDisplayName(displayName);

            // ИЗМЕНЕНО: Используем ChatUtil
            List<String> lore = itemConfig.getStringList("lore").stream()
                    .map(ChatUtil::color) // Применяем ChatUtil к каждой строке
                    .collect(Collectors.toList());
            meta.setLore(lore);
            item.setItemMeta(meta);

            String clickAction = itemConfig.getString("click_action");

            if (itemConfig.contains("slot")) {
                int slot = itemConfig.getInt("slot");
                this.menuInventory.setItem(slot, item);
                if (clickAction != null) {
                    this.slotActions.put(slot, clickAction);
                }
            } else if (itemConfig.contains("slots")) {
                for (int slot : itemConfig.getIntegerList("slots")) {
                    if (slot < menuSize) {
                        this.menuInventory.setItem(slot, item);
                        if (clickAction != null) {
                            this.slotActions.put(slot, clickAction);
                        }
                    }
                }
            }
        }
    }

    public Inventory getMenuInventory() {
        return menuInventory;
    }

    public String getActionForSlot(int slot) {
        return slotActions.get(slot);
    }

    @Override
    public Inventory getInventory() {
        return this.menuInventory;
    }
}