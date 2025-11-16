package com.mrrezik.artefact.util;

import com.mrrezik.artefact.Artefact;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class NBTUtil {

    public static final NamespacedKey ARTEFACT_ID_KEY = new NamespacedKey(Artefact.getInstance(), "artefact_id");
    public static final NamespacedKey ARTEFACT_VALUE_KEY = new NamespacedKey(Artefact.getInstance(), "artefact_value");
    // НОВЫЙ КЛЮЧ
    public static final NamespacedKey ARTEFACT_CURRENCY_KEY = new NamespacedKey(Artefact.getInstance(), "artefact_currency");

    // Установить NBT-тег (String)
    public static ItemStack setString(ItemStack item, NamespacedKey key, String value) {
        if (item == null || item.getItemMeta() == null) return item;
        ItemMeta meta = item.getItemMeta();
        meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, value);
        item.setItemMeta(meta);
        return item;
    }

    // Получить NBT-тег (String)
    public static String getString(ItemStack item, NamespacedKey key) {
        if (item == null || item.getItemMeta() == null) return null;
        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer container = meta.getPersistentDataContainer();
        if (container.has(key, PersistentDataType.STRING)) {
            return container.get(key, PersistentDataType.STRING);
        }
        return null;
    }

    // Установить NBT-тег (Double)
    public static ItemStack setDouble(ItemStack item, NamespacedKey key, double value) {
        if (item == null || item.getItemMeta() == null) return item;
        ItemMeta meta = item.getItemMeta();
        meta.getPersistentDataContainer().set(key, PersistentDataType.DOUBLE, value);
        item.setItemMeta(meta);
        return item;
    }

    // Получить NBT-тег (Double)
    public static Double getDouble(ItemStack item, NamespacedKey key) {
        if (item == null || item.getItemMeta() == null) return null;
        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer container = meta.getPersistentDataContainer();
        if (container.has(key, PersistentDataType.DOUBLE)) {
            return container.get(key, PersistentDataType.DOUBLE);
        }
        return null;
    }

    // Проверить, является ли предмет артефактом
    public static boolean isArtefact(ItemStack item) {
        return getString(item, ARTEFACT_ID_KEY) != null;
    }
}