package com.mrrezik.artefact.managers;

import com.mrrezik.artefact.Artefact;
import com.mrrezik.artefact.util.ChatUtil; // ИЗМЕНЕНО
import com.mrrezik.artefact.util.NBTUtil;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class ArtefactManager {

    private final Artefact plugin;
    private ConfigurationSection artefactsConfigSection;

    public ArtefactManager(Artefact plugin) {
        this.plugin = plugin;
        loadArtefacts();
    }

    public void loadArtefacts() {
        this.artefactsConfigSection = plugin.getConfig().getConfigurationSection("artefacts");
    }

    // Метод getArtefact ТЕПЕРЬ БЕЗ АРГУМЕНТА VALUE
    public ItemStack getArtefact(String artefactId, int amount) {
        if (artefactsConfigSection == null) {
            plugin.getLogger().warning("Секция 'artefacts' в config.yml не найдена!");
            return null;
        }
        ConfigurationSection configSection = artefactsConfigSection.getConfigurationSection(artefactId);
        if (configSection == null) {
            return null; // Артефакт не найден в конфиге
        }

        Material material = Material.matchMaterial(configSection.getString("material", "STONE"));
        ItemStack item = new ItemStack(material != null ? material : Material.STONE, amount);

        // --- Генерируем случайную ценность ---
        double min = configSection.getDouble("value.min", 1.0);
        double max = configSection.getDouble("value.max", 1.0);
        double value = (min == max) ? min : ThreadLocalRandom.current().nextDouble(min, max);

        // --- Получаем тип валюты ---
        String currencyType = configSection.getString("currency", "PLAYER_POINTS").toUpperCase();

        // --- Применяем NBT ---
        item = NBTUtil.setString(item, NBTUtil.ARTEFACT_ID_KEY, artefactId);
        item = NBTUtil.setDouble(item, NBTUtil.ARTEFACT_VALUE_KEY, value);
        item = NBTUtil.setString(item, NBTUtil.ARTEFACT_CURRENCY_KEY, currencyType); // НОВОЕ NBT

        // Обновляем лор и имя
        updateItemDisplay(item, configSection, value, currencyType); // ИЗМЕНЕНА СИГНАТУРА

        return item;
    }

    /**
     * Обновляет отображение предмета (имя и лор) на основе ЕГО NBT-данных.
     * Этот метод вызывается при создании и при "росте" артефакта.
     */
    public void updateItemDisplay(ItemStack item, ConfigurationSection configSection, double value, String currencyType) {
        if (item == null || item.getItemMeta() == null || configSection == null) return;

        ItemMeta meta = item.getItemMeta();

        // Устанавливаем имя
        String displayName = ChatUtil.color(configSection.getString("display_name", "Артефакт"));
        meta.setDisplayName(displayName);

        // Рассчитываем цену
        int pointsPerValue = plugin.getConfig().getInt("points-per-value", 100);
        int cost = (int) (value * pointsPerValue);

        // Получаем название валюты из конфига (для плейсхолдера %currency%)
        String currencyName = plugin.getConfig().getString("currency-names." + currencyType, currencyType);

        // Устанавливаем лор, заменяя ВСЕ плейсхолдеры
        List<String> lore = configSection.getStringList("lore").stream()
                .map(line -> ChatUtil.color( // ИСПОЛЬЗУЕМ ChatUtil
                        line.replace("%chance%", String.format("%.2f", value))
                                .replace("%cost%", String.valueOf(cost))
                                .replace("%currency%", currencyName) // НОВЫЙ ПЛЕЙСХОЛДЕР
                ))
                .collect(Collectors.toList());

        meta.setLore(lore);
        item.setItemMeta(meta);
    }

    // Метод, который использует ArtefactGrowthTask для обновления предмета
    public void updateGrowingArtefact(ItemStack item, double valueIncrease, double maxValue) {
        if (!NBTUtil.isArtefact(item)) return;

        String id = NBTUtil.getString(item, NBTUtil.ARTEFACT_ID_KEY);
        Double oldValue = NBTUtil.getDouble(item, NBTUtil.ARTEFACT_VALUE_KEY);
        String currencyType = NBTUtil.getString(item, NBTUtil.ARTEFACT_CURRENCY_KEY); // НОВОЕ

        if (id == null || oldValue == null || currencyType == null) return;

        // Проверяем, достигнут ли лимит
        if (maxValue != -1 && oldValue >= maxValue) {
            return; // Ценность уже на максимуме
        }

        double newValue = oldValue + valueIncrease;
        if (maxValue != -1 && newValue > maxValue) {
            newValue = maxValue;
        }

        // 1. Обновляем NBT
        NBTUtil.setDouble(item, NBTUtil.ARTEFACT_VALUE_KEY, newValue);

        // 2. Обновляем лор
        ConfigurationSection configSection = artefactsConfigSection.getConfigurationSection(id);
        if (configSection != null) {
            // Передаем все данные для обновления лора
            updateItemDisplay(item, configSection, newValue, currencyType);
        }
    }

    public Set<String> getArtefactIds() {
        if (artefactsConfigSection == null) {
            return Set.of();
        }
        return artefactsConfigSection.getKeys(false);
    }
}